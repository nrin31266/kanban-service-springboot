package com.rin.kanban.repository.custom;

import com.rin.kanban.pojo.RatingResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
@Repository
@Slf4j
public class RatingCustomRepositoryImp implements RatingCustomRepository {
    private final MongoTemplate mongoTemplate;

    @Override
    public RatingResult getRatingByProductId(String productId) {
        MatchOperation matchRating = Aggregation.match(Criteria.where("productId").is(productId));

        // Group theo productId và tính tổng số lượt đánh giá và giá trị trung bình
        GroupOperation groupByProductId = Aggregation.group("productId")
                .count().as("countRating")  // Đếm số lượng đánh giá
                .avg("rating").as("averageRating");  // Tính giá trị trung bình của rating

        // Build Aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                matchRating,            // Match các Rating liên quan đến productId
                groupByProductId       // Group và tính tổng / trung bình
        );

        AggregationResults<RatingResult> results = mongoTemplate.aggregate(aggregation, "rating", RatingResult.class);


        // Xử lý kết quả và trả về RatingResult
        if (!results.getMappedResults().isEmpty()) {
            RatingResult result = results.getMappedResults().getFirst();
            if (result != null) {
                return result;
            }
        }


        return new RatingResult(0, BigDecimal.ZERO);
    }
}
