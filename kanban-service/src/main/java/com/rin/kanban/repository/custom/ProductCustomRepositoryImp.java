package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.request.FilterProductsRequest;
import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.types.Decimal128;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImp implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Page<Product> searchProducts(FilterProductsRequest filterRequest, int page, int size) {
        if (filterRequest.getMaxPrice() != null || filterRequest.getMinPrice() != null) {
            return searchLookupDocumentSubProduct(filterRequest, page, size);
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").ne(true));

        if (filterRequest.getCategoryIds() != null) {
            List<String> categoryIds = Arrays.asList(filterRequest.getCategoryIds().split(","));
            query.addCriteria(Criteria.where("categoryIds").all(categoryIds));
        }

        query.with(Sort.by(Sort.Direction.DESC, "updatedAt"));
        long total = mongoTemplate.count(query, Product.class);

        query.skip((long) (page - 1) * size);
        query.limit(size);

        List<Product> products = mongoTemplate.find(query, Product.class);
        Pageable pageable = PageRequest.of(page - 1, size);

        return new PageImpl<>(products, pageable, total);
    }

    private Page<Product> searchLookupDocumentSubProduct(FilterProductsRequest request, int page, int size) {
        // Tạo danh sách các AggregationOperation
        List<AggregationOperation> operations = new ArrayList<>();
        // Lookup: Thực hiện join với collection sub-products
        LookupOperation lookupOperation = Aggregation.lookup("sub-products", "_id", "productId", "subProducts");
        operations.add(lookupOperation);
        // Chỉ lấy sản phẩm có isDeleted là false hoặc null
        MatchOperation matchProductIsDeleted = Aggregation.match(Criteria.where("isDeleted").ne(true));
        operations.add(matchProductIsDeleted);
        if (request.getCategoryIds() != null) {
            List<String> categoryIds = Arrays.asList(request.getCategoryIds().split(","));
            MatchOperation matchCategoryIds = Aggregation.match(Criteria.where("categoryIds").all(categoryIds));
            operations.add(matchCategoryIds);
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
        // Sắp xếp theo updatedAt theo thứ tự giảm dần
        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "updatedAt")));
        // ** Step 1: Tính totalElements trước khi phân trang **
        List<AggregationOperation> countOperations = new ArrayList<>(operations);
        countOperations.add(Aggregation.count().as("totalCount"));
        Aggregation countAggregation = Aggregation.newAggregation(countOperations);
        AggregationResults<Document> countResults = mongoTemplate.aggregate(countAggregation, "products", Document.class);
        // Thay đổi cách lấy giá trị totalCount từ kết quả
        long totalElements;
        if (!countResults.getMappedResults().isEmpty()) {
            Object totalCountObj = countResults.getMappedResults().getFirst().get("totalCount");
            if (totalCountObj instanceof Integer) {
                totalElements = ((Integer) totalCountObj).longValue();  // Convert Integer to Long
            } else if (totalCountObj instanceof Long) {
                totalElements = (Long) totalCountObj;  // If type = Long
            } else {
                totalElements = 0L;
            }
        } else {
            totalElements = 0L;
        }
        // ** Step 2: Lấy dữ liệu với phân trang **
        operations.add(Aggregation.skip((long) (page - 1) * size));
        operations.add(Aggregation.limit(size));
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<Product> products = mongoTemplate.aggregate(aggregation, "products", Product.class).getMappedResults();
        // Phân trang với Pageable
        Pageable pageable = PageRequest.of(page - 1, size);
        return PageableExecutionUtils.getPage(products, pageable, () -> totalElements);
    }


//
}
