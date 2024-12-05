package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.request.FilterProductsRequest;
import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.dto.response.ProductResponse;
import com.rin.kanban.entity.Product;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.pojo.BestSellerResult;
import com.rin.kanban.pojo.ProductResult;
import com.rin.kanban.pojo.RatingResult;
import com.rin.kanban.repository.SubProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.bson.conversions.Bson;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImp implements ProductCustomRepository {

    private final MongoTemplate mongoTemplate;
    private final SubProductRepository subProductRepository;
    private final SubProductRepositoryImp subProductRepositoryImp;

    @Override
    public Page<Product> searchProducts(FilterProductsRequest request, int page, int size) {
        List<AggregationOperation> operations = new ArrayList<>();
//        operations.add(createCategoriesLookup());
//        operations.add(createSupplierLookup());

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
        if(request.getSearch() != null) {
            MatchOperation matchSearch = Aggregation.match(Criteria.where("slug").regex(".*" + request.getSearch() + ".*", "i"));
            operations.add(matchSearch);
        }
        AggregationOperation addFinalPriceField = Aggregation.addFields()
                .addField("subProducts")
                .withValue(
                        new AggregationExpression() {
                            @Override
                            public Document toDocument(AggregationOperationContext context) {
                                return new Document("$map", new Document()
                                        .append("input", "$subProducts")  // Lấy danh sách subProducts
                                        .append("as", "subProduct")  // Đặt alias cho từng phần tử
                                        .append("in", new Document("$mergeObjects", Arrays.asList(
                                                "$$subProduct",  // Giữ lại tất cả các trường hiện tại của subProduct
                                                new Document("finalPrice", new Document("$ifNull", Arrays.asList(
                                                        "$$subProduct.discount",  // Kiểm tra nếu discount là null
                                                        "$$subProduct.price"  // Dùng price nếu discount là null
                                                )))
                                        ))));
                            }
                        }
                )
                .build();

        operations.add(addFinalPriceField);
        // Kiểm tra ba trường hợp minPrice, maxPrice, và minPrice với maxPrice:
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            // Truy vấn với minPrice và maxPrice, và sử dụng `finalPrice` cho subProducts
            MatchOperation matchByPriceRange = Aggregation.match(
                    Criteria.where("subProducts.finalPrice")
                            .gte(new Decimal128(request.getMinPrice()))
                            .lte(new Decimal128(request.getMaxPrice()))
            );
            operations.add(matchByPriceRange);
        } else if (request.getMinPrice() != null) {
            // Truy vấn chỉ với minPrice, và sử dụng `finalPrice`
            MatchOperation matchByMinPrice = Aggregation.match(
                    Criteria.where("subProducts.finalPrice")
                            .gte(new Decimal128(request.getMinPrice()))
            );
            operations.add(matchByMinPrice);
        } else if (request.getMaxPrice() != null) {
            // Truy vấn chỉ với maxPrice, và sử dụng `finalPrice`
            MatchOperation matchByMaxPrice = Aggregation.match(
                    Criteria.where("subProducts.finalPrice")
                            .lte(new Decimal128(request.getMaxPrice()))
            );
            operations.add(matchByMaxPrice);
        }

        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "updatedAt")));
        List<AggregationOperation> countOperations = new ArrayList<>(operations);
        countOperations.add(Aggregation.count().as("totalCount"));
        Aggregation countAggregation = Aggregation.newAggregation(countOperations);
        AggregationResults<Document> countResults = mongoTemplate.aggregate(countAggregation, "products", Document.class);
        long totalElements;
        if (!countResults.getMappedResults().isEmpty()) {
            Object totalCountObj = countResults.getMappedResults().getFirst().get("totalCount");
            if (totalCountObj instanceof Integer) {
                totalElements = ((Integer) totalCountObj).longValue();  // Convert Integer to Long
            } else if (totalCountObj instanceof Long) {
                totalElements = (Long) totalCountObj;
            } else {
                totalElements = 0L;
            }
        } else {
            totalElements = 0L;
        }

        operations.add(Aggregation.skip((long) (page - 1) * size));
        operations.add(Aggregation.limit(size));
        Aggregation aggregation = Aggregation.newAggregation(operations);


        List<Product> products = mongoTemplate.aggregate(aggregation, "products", Product.class).getMappedResults();

        log.info(products.toString());


        Pageable pageable = PageRequest.of(page - 1, size);
        return PageableExecutionUtils.getPage(products, pageable, () -> totalElements);
    }

    private LookupOperation createSupplierLookup() {
        return Aggregation.lookup("suppliers", "supplierId", "_id", "supplier");
    }

    private LookupOperation createCategoriesLookup() {
        return Aggregation.lookup("category", "categoryIds", "_id", "categories");
    }





    @Override
    public List<String> getTopSoldProducts() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("isComplete").is(true)), // Lọc đơn hàng đã hoàn thành
                Aggregation.lookup("order-product", "_id", "orderId", "orderProducts"), // Lookup sang order-products
                Aggregation.unwind("orderProducts"), // Tách orderProducts
                Aggregation.group("orderProducts.productId") // Nhóm theo productId
                        .sum("orderProducts.count").as("totalSold"), // Tổng số lượng đã bán
                Aggregation.sort(Sort.Direction.DESC, "totalSold"), // Sắp xếp giảm dần
                Aggregation.limit(8) // Lấy 8 sản phẩm đầu tiên
        );

//        AggregationResults<BestSellerResult> results = mongoTemplate.aggregate(aggregation, "orders", BestSellerResult.class);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "orders", Document.class);

        // Trích xuất danh sách productId từ kết quả
        return results.getMappedResults().stream()
                .map(doc -> doc.getString("_id")) // Lấy _id (productId)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductResponse> getProductById(String productId) {
        List<AggregationOperation> operations = new ArrayList<>();

        // Lookup: Thực hiện join với collection sub-products
        LookupOperation lookupOperation = Aggregation.lookup("sub-products", "_id", "productId", "subProducts");
        operations.add(lookupOperation);

        MatchOperation matchProductIsDeleted = Aggregation.match(Criteria.where("isDeleted").ne(true));
        operations.add(matchProductIsDeleted);

        // Match theo productId
        MatchOperation matchProductId = Aggregation.match(Criteria.where("_id").is(productId));
        operations.add(matchProductId);

        // Thực hiện lookup các thông tin liên quan đến category và supplier
        operations.add(createCategoriesLookup());
        operations.add(createSupplierLookup());

        // Chỉ lấy 1 sản phẩm, sử dụng limit(1)
        operations.add(Aggregation.limit(1));

        // Tiến hành truy vấn
        Aggregation aggregation = Aggregation.newAggregation(operations);
        List<ProductResponse> products = mongoTemplate.aggregate(aggregation, "products", ProductResponse.class).getMappedResults();

        // Kiểm tra lại kết quả trả về, log sản phẩm để kiểm tra
        if (products.isEmpty()) {
            log.warn("No product found with id: " + productId);
            return Optional.empty();
        }

        log.info("Product found: " + products.get(0));  // Log sản phẩm đầu tiên
        return Optional.of(products.get(0));  // Chỉ trả về sản phẩm đầu tiên
    }



    @Override
    public Page<ProductResponse> searchProductsV2(FilterProductsRequest request, int page, int size) {
        List<AggregationOperation> operations = new ArrayList<>();
        operations.add(createCategoriesLookup());
        operations.add(createSupplierLookup());

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
        if(request.getSearch() != null) {
            MatchOperation matchSearch = Aggregation.match(Criteria.where("slug").regex(".*" + request.getSearch() + ".*", "i"));
            operations.add(matchSearch);
        }



        AggregationOperation addFinalPriceField = Aggregation.addFields()
                .addField("subProducts")
                .withValue(
                        new AggregationExpression() {
                            @Override
                            public Document toDocument(AggregationOperationContext context) {
                                return new Document("$map", new Document()
                                        .append("input", "$subProducts")  // Lấy danh sách subProducts
                                        .append("as", "subProduct")  // Đặt alias cho từng phần tử
                                        .append("in", new Document("$mergeObjects", Arrays.asList(
                                                "$$subProduct",  // Giữ lại tất cả các trường hiện tại của subProduct
                                                new Document("finalPrice", new Document("$ifNull", Arrays.asList(
                                                        "$$subProduct.discount",  // Kiểm tra nếu discount là null
                                                        "$$subProduct.price"  // Dùng price nếu discount là null
                                                )))
                                        ))));
                            }
                        }
                )
                .build();

        operations.add(addFinalPriceField);

        AggregationOperation addMinMaxFields = Aggregation.addFields()
                .addField("minPrice")
                .withValue(new Document("$min", "$subProducts.finalPrice"))
                .addField("maxPrice")
                .withValue(new Document("$max", "$subProducts.finalPrice"))
                .build();

        operations.add(addMinMaxFields);



        // Kiểm tra ba trường hợp minPrice, maxPrice, và minPrice với maxPrice:
        if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            // Truy vấn với minPrice và maxPrice, và sử dụng `finalPrice` cho subProducts
            MatchOperation matchByPriceRange = Aggregation.match(
                    Criteria.where("subProducts.finalPrice")
                            .gte(new Decimal128(request.getMinPrice()))
                            .lte(new Decimal128(request.getMaxPrice()))
            );
            operations.add(matchByPriceRange);
        } else if (request.getMinPrice() != null) {
            // Truy vấn chỉ với minPrice, và sử dụng `finalPrice`
            MatchOperation matchByMinPrice = Aggregation.match(
                    Criteria.where("subProducts.finalPrice")
                            .gte(new Decimal128(request.getMinPrice()))
            );
            operations.add(matchByMinPrice);
        } else if (request.getMaxPrice() != null) {
            // Truy vấn chỉ với maxPrice, và sử dụng `finalPrice`
            MatchOperation matchByMaxPrice = Aggregation.match(
                    Criteria.where("subProducts.finalPrice")
                            .lte(new Decimal128(request.getMaxPrice()))
            );
            operations.add(matchByMaxPrice);
        }

        operations.add(Aggregation.sort(Sort.by(Sort.Direction.DESC, "updatedAt")));
        List<AggregationOperation> countOperations = new ArrayList<>(operations);
        countOperations.add(Aggregation.count().as("totalCount"));
        Aggregation countAggregation = Aggregation.newAggregation(countOperations);
        AggregationResults<Document> countResults = mongoTemplate.aggregate(countAggregation, "products", Document.class);
        long totalElements;
        if (!countResults.getMappedResults().isEmpty()) {
            Object totalCountObj = countResults.getMappedResults().getFirst().get("totalCount");
            if (totalCountObj instanceof Integer) {
                totalElements = ((Integer) totalCountObj).longValue();  // Convert Integer to Long
            } else if (totalCountObj instanceof Long) {
                totalElements = (Long) totalCountObj;
            } else {
                totalElements = 0L;
            }
        } else {
            totalElements = 0L;
        }

        operations.add(Aggregation.skip((long) (page - 1) * size));
        operations.add(Aggregation.limit(size));
        Aggregation aggregation = Aggregation.newAggregation(operations);


        List<ProductResponse> products = mongoTemplate.aggregate(aggregation, "products", ProductResponse.class).getMappedResults();

        log.info(products.toString());


        Pageable pageable = PageRequest.of(page - 1, size);
        return PageableExecutionUtils.getPage(products, pageable, () -> totalElements);
    }







//
}
