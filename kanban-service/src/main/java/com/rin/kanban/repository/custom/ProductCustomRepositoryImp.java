package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.dto.response.ProductHasSubProductsResponse;
import com.rin.kanban.entity.Category;
import com.rin.kanban.entity.Product;
import com.rin.kanban.entity.SubProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Decimal128;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImp implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> findAllByFilterValues(ProductsFilterValuesRequest request) {
        List<AggregationOperation> operations = new ArrayList<>();

        // Lookup: Perform a join with the sub-products collection
        LookupOperation lookupOperation = Aggregation.lookup("sub-products", "_id", "productId", "subProducts");
        operations.add(lookupOperation);

        // Filter by category IDs if provided
        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            MatchOperation matchByCategoryIds = Aggregation.match(Criteria.where("categoryIds").all(request.getCategoryIds()));
            operations.add(matchByCategoryIds);
        }

        // Filter by colors if provided
        if (request.getColors() != null && !request.getColors().isEmpty()) {
            MatchOperation matchByColor = Aggregation.match(Criteria.where("subProducts.color").in(request.getColors()));
            operations.add(matchByColor);
        }

        // Filter by sizes if provided
        if (request.getSizes() != null && !request.getSizes().isEmpty()) {
            MatchOperation matchBySize = Aggregation.match(Criteria.where("subProducts.size").in(request.getSizes()));
            operations.add(matchBySize);
        }

        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            MatchOperation matchByPrice = Aggregation.match(Criteria.where("subProducts").elemMatch(Criteria.where("price")
                    .gte(new Decimal128(request.getMinPrice()))
                    .lte(new Decimal128(request.getMaxPrice()))));
            operations.add(matchByPrice);
        } else if (request.getMinPrice() != null) {
            MatchOperation matchByMinPrice = Aggregation.match(Criteria.where("subProducts").elemMatch(Criteria.where("price")
                    .gte(new Decimal128(request.getMinPrice()))));
            operations.add(matchByMinPrice);
        } else if (request.getMaxPrice() != null) {
            MatchOperation matchByMaxPrice = Aggregation.match(Criteria.where("subProducts").elemMatch(Criteria.where("price")
                    .lte(new Decimal128(request.getMaxPrice()))));
            operations.add(matchByMaxPrice);
        }



        // Sorting and pagination
        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "updatedAt")));
        operations.add(Aggregation.skip((long) (request.getPage() - 1) * request.getSize()));
        operations.add(Aggregation.limit(request.getSize()));

        // Handle aggregation
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Product> products = mongoTemplate.aggregate(aggregation, "products", Product.class).getMappedResults();

        // Calculate total elements for pagination
        long totalElements = mongoTemplate.count(new Query(), Product.class);
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());

        // Return paginated result
        return PageableExecutionUtils.getPage(products, pageable, () -> totalElements);
    }


}
