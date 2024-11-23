package com.rin.kanban.repository.custom;

import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.repository.SubProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class SubProductRepositoryImp implements SubProductCustomRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<SubProduct> findMaxPrice(String productId) {
        Query query = queryFindMaxPriceByProductId(productId);
        SubProduct subProduct = mongoTemplate.findOne(query, SubProduct.class);
        return Optional.ofNullable(subProduct);
    }

    @Override
    public Optional<SubProduct> findMinPrice(String productId) {
        Query priceQuery = queryFindMinPriceByProductId(productId);
        Query discountQuery = queryFindMinDiscountByProductId(productId);
        return getEffectiveMinPrice(priceQuery, discountQuery);

    }

    @Override
    public Optional<SubProduct> findMaxPrice() {
        Query query = queryFindMaxPrice();
        SubProduct subProduct = mongoTemplate.findOne(query, SubProduct.class);
        return Optional.ofNullable(subProduct);
    }

    @Override
    public Optional<SubProduct> findMinPrice() {
        Query priceQuery = queryFindMinPrice();
        Query discountQuery = queryFindMinDiscount();
        return getEffectiveMinPrice(priceQuery, discountQuery);
    }

    private Optional<SubProduct> getEffectiveMinPrice(Query priceQuery, Query discountQuery) {
        SubProduct minPriceProduct = mongoTemplate.findOne(priceQuery, SubProduct.class);
        SubProduct minDiscountProduct = mongoTemplate.findOne(discountQuery, SubProduct.class);
        log.info(minDiscountProduct.toString());

        if (minPriceProduct != null && minDiscountProduct != null) {
            BigDecimal price = minPriceProduct.getPrice();
            BigDecimal discount = minDiscountProduct.getDiscount();

            // Kiểm tra giá trị null của discount và price
            if (price != null && discount != null && discount.compareTo(price) < 0) {
                minDiscountProduct.setPrice(discount);
                return Optional.of(minDiscountProduct);
            }
            return Optional.of(minPriceProduct);
        }
        return Optional.ofNullable(minPriceProduct);
    }



    private Query queryFindMinPriceByProductId(String productId) {
        Query query = queryFindMinPrice();
        query.addCriteria(Criteria.where("productId").is(productId));
        return query;
    }

    private Query queryFindMaxPriceByProductId(String productId) {
        Query query = queryFindMaxPrice();
        query.addCriteria(Criteria.where("productId").is(productId));
        return query;
    }

    private Query queryFindMinDiscountByProductId(String productId) {
        Query query = queryFindMinDiscount();
        query.addCriteria(Criteria.where("productId").is(productId));
        return query;
    }

    private Query queryFindMaxPrice() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").ne(true));
        query.with(Sort.by(Sort.Direction.DESC, "price"));
        query.limit(1);
        return query;
    }

    private Query queryFindMinDiscount() {
        Query discountQuery = new Query();
        discountQuery.addCriteria(Criteria.where("isDeleted").ne(true));
        discountQuery.with(Sort.by(Sort.Direction.ASC, "discount"));
        discountQuery.limit(1);
        return discountQuery;
    }

    private Query queryFindMinPrice() {
        Query query = new Query();
        query.addCriteria(Criteria.where("isDeleted").ne(true));
        query.with(Sort.by(Sort.Direction.ASC, "price"));
        query.limit(1);
        return query;
    }
}
