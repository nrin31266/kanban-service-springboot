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
public class SubProductRepositoryImp implements SubProductCustomRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public Optional<SubProduct> findMaxPriceSubProduct(String productId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("productId").is(productId)
                .orOperator(Criteria.where("isDeleted").is(false),
                        Criteria.where("isDeleted").is(null)));
        query.with(Sort.by(Sort.Direction.DESC, "price"));
        query.limit(1);

        SubProduct subProduct = mongoTemplate.findOne(query, SubProduct.class);
        return Optional.ofNullable(subProduct);
    }

    @Override
    public Optional<SubProduct> findMinPriceSubProduct(String productId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("productId").is(productId)
                .orOperator(Criteria.where("isDeleted").is(null)
                ,Criteria.where("isDeleted").is(false)));
        query.with(Sort.by(Sort.Direction.ASC, "price"));
        query.limit(1);
        SubProduct minPriceProduct = mongoTemplate.findOne(query, SubProduct.class);

        Query discountQuery = new Query();
        discountQuery.addCriteria(Criteria.where("productId").is(productId)
                .orOperator(Criteria.where("isDeleted").is(null),
                        Criteria.where("isDeleted").is(false))
                .andOperator(Criteria.where("discount").ne(null)));

        discountQuery.with(Sort.by(Sort.Direction.ASC, "discount"));
        discountQuery.limit(1);

        SubProduct minDiscountProduct = mongoTemplate.findOne(discountQuery, SubProduct.class);

        if (minPriceProduct != null && minDiscountProduct != null){
            log.info(minDiscountProduct.toString());
            BigDecimal price = minPriceProduct.getPrice();
            BigDecimal discount = minDiscountProduct.getDiscount();
            if(discount.compareTo(price) < 0){
                minDiscountProduct.setPrice(discount);
                return Optional.of(minDiscountProduct);
            }else{
                return Optional.of(minPriceProduct);
            }
        }else {
            return Optional.ofNullable(minPriceProduct);
        }
    }
}
