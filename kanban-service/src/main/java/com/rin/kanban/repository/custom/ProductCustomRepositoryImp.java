package com.rin.kanban.repository.custom;

import com.rin.kanban.dto.request.ProductsFilterValuesRequest;
import com.rin.kanban.entity.Category;
import com.rin.kanban.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Repository
public class ProductCustomRepositoryImp implements ProductCustomRepository{

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Product> findAllByFilterValues(ProductsFilterValuesRequest request, Pageable pageable) {
        Query query = new Query();
        // Xử lý categories
        if (request.getCategoriesId() != null && !request.getCategoriesId().isEmpty()) {
            List<Category> categories = mongoTemplate.find(Query.query(Criteria.where("id").in(request.getCategoriesId())), Category.class);
            Set<Category> categorySet = Set.copyOf(categories);
            query.addCriteria(Criteria.where("categories").in(categorySet));
        }

        return mongoTemplate.find(query, Product.class);
    }
//    if(request.getColors()!=null && !request.getColors().isEmpty()){
//        query.addCriteria(Criteria.where("color").is(request.getColors()));
//    }
//        if(request.getSizes()!=null && !request.getSizes().isEmpty()){
//        query.addCriteria(Criteria.where("sizes").is(request.getSizes()));
//    }
//        if (request.getMinPrice()!=null && request.getMaxPrice()!=null && request.getMinPrice().compareTo(request.getMaxPrice()) < 0){
//        query.addCriteria(Criteria.where("price").gte(request.getMinPrice()).lte(request.getMaxPrice()));
//    }else if(request.getMinPrice()!=null && request.getMaxPrice()==null){
//        query.addCriteria(Criteria.where("price").gte(request.getMinPrice()));
//    }else if (request.getMinPrice()==null && request.getMaxPrice()!=null){
//        query.addCriteria(Criteria.where("price").gte(request.getMaxPrice()));
//    }
}
