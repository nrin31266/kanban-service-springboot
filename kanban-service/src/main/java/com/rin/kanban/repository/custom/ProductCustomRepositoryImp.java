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
    public Page<Product>  searchProducts(FilterProductsRequest filterRequest, int page, int size) {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").ne(true));

        if (filterRequest.getCategoryIds() != null) {
            List<String> categoryIds = Arrays.asList(filterRequest.getCategoryIds().split(","));
            log.info(categoryIds.toString());
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

//    @Override
//    public Page<Product> findAllByFilterValues(ProductsFilterValuesRequest request) {
//        List<AggregationOperation> operations = new ArrayList<>();
//        // Lookup: Perform a join with the sub-products collection
//        LookupOperation lookupOperation = Aggregation.lookup("sub-products", "_id", "productId", "subProducts");
//        operations.add(lookupOperation);
//        // Filter by category IDs if provided
//        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
//            MatchOperation matchByCategoryIds = Aggregation.match(Criteria.where("categoryIds").all(request.getCategoryIds()));
//            operations.add(matchByCategoryIds);
//        }
//        // Only get product has isDeleted is false or null
//        MatchOperation matchProductIsDeleted = Aggregation.match(new Criteria().orOperator(
//                Criteria.where("isDeleted").is(null),
//                Criteria.where("isDeleted").is(false)
//        ));
//        operations.add(matchProductIsDeleted);
////        // Only get sub-products where isDeleted is false or null
////        MatchOperation matchBySubProductIsDeleted = Aggregation.match(Criteria.where("subProducts")
////                .elemMatch(new Criteria().orOperator(
////                        Criteria.where("isDeleted").is(null),
////                        Criteria.where("isDeleted").is(false)
////                )));
////        operations.add(matchBySubProductIsDeleted);
//
////        // Filter out deleted sub-products using unwind and group
////        UnwindOperation unwindSubProducts = Aggregation.unwind("subProducts", "unwoundSubProduct", true); // preserve products without sub-products
////        operations.add(unwindSubProducts);
////
////        MatchOperation matchSubProductIsDeleted = Aggregation.match(new Criteria().orOperator(
////                Criteria.where("unwoundSubProduct.isDeleted").is(null),
////                Criteria.where("unwoundSubProduct.isDeleted").is(false)
////        ));
////        operations.add(matchSubProductIsDeleted);
////
////        // Group back to reassemble products with filtered sub-products
////        GroupOperation groupProducts = Aggregation.group("$_id")
////                .first("title").as("title")
//////                .first("content").as("content")
////                .first("images").as("images")
//////                .first("supplierId").as("supplierId")
////                .first("slug").as("slug")
////                .first("description").as("description")
//////                .first("supplierId").as("supplierId")
//////                .first("createdAt").as("createdAt")
//////                .first("updatedAt").as("updatedAt")
////                .first("categoryIds").as("categoryIds")
//////                .first("subProducts").as("subProducts")
////                .push("unwoundSubProduct").as("filteredSubProducts");
////        // reassemble sub-products
////        operations.add(groupProducts);
//
//
//        // Filter by colors if provided
//        if (request.getColors() != null && !request.getColors().isEmpty()) {
//            MatchOperation matchByColor = Aggregation.match(Criteria.where("subProducts.color").in(request.getColors()));
//            operations.add(matchByColor);
//        }
//        // Filter by sizes if provided
//        if (request.getSizes() != null && !request.getSizes().isEmpty()) {
//            MatchOperation matchBySize = Aggregation.match(Criteria.where("subProducts.size").in(request.getSizes()));
//            operations.add(matchBySize);
//        }
//        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
//            MatchOperation matchByPrice = Aggregation.match(Criteria.where("subProducts").elemMatch(Criteria.where("price")
//                    .gte(new Decimal128(request.getMinPrice()))
//                    .lte(new Decimal128(request.getMaxPrice()))));
//            operations.add(matchByPrice);
//        } else if (request.getMinPrice() != null) {
//            MatchOperation matchByMinPrice = Aggregation.match(Criteria.where("subProducts").elemMatch(Criteria.where("price")
//                    .gte(new Decimal128(request.getMinPrice()))));
//            operations.add(matchByMinPrice);
//        } else if (request.getMaxPrice() != null) {
//            MatchOperation matchByMaxPrice = Aggregation.match(Criteria.where("subProducts").elemMatch(Criteria.where("price")
//                    .lte(new Decimal128(request.getMaxPrice()))));
//            operations.add(matchByMaxPrice);
//        }
//        // Sorting and pagination
//        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "updatedAt")));
//
//        // ** Step 1: Tính totalElements trước khi phân trang **
//        List<AggregationOperation> countOperations = new ArrayList<>(operations);
//        countOperations.add(Aggregation.count().as("totalCount"));
//
//        Aggregation countAggregation = Aggregation.newAggregation(countOperations);
//        AggregationResults<Document> countResults = mongoTemplate.aggregate(countAggregation, "products", Document.class);
//
//        // Thay đổi cách lấy giá trị totalCount từ kết quả
//        long totalElements;
//        if (!countResults.getMappedResults().isEmpty()) {
//            Object totalCountObj = countResults.getMappedResults().getFirst().get("totalCount");
//            if (totalCountObj instanceof Integer) {
//                totalElements = ((Integer) totalCountObj).longValue();  // Convert Integer to Long
//            } else if (totalCountObj instanceof Long) {
//                totalElements = (Long) totalCountObj;  // If type = Long
//            } else {
//                totalElements = 0L;
//            }
//        } else {
//            totalElements = 0L;
//        }
//
//        // ** Step 2: Lấy dữ liệu với phân trang **
//        operations.add(Aggregation.skip((long) (request.getPage() - 1) * request.getSize()));
//        operations.add(Aggregation.limit(request.getSize()));
//
//        Aggregation aggregation = Aggregation.newAggregation(operations);
//        List<Product> products = mongoTemplate.aggregate(aggregation, "products", Product.class).getMappedResults();
//
//        // Phân trang với Pageable
//        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
//
//        return PageableExecutionUtils.getPage(products, pageable, () -> totalElements);
//    }
}
