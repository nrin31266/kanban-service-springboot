package com.rin.kanban.repository;

import com.rin.kanban.dto.response.ExportSupplierDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class SuppliersCustomRepositoryImpl implements SuppliersCustomRepository {
    private final MongoTemplate mongoTemplate;
    @Override
    public List<ExportSupplierDataResponse> findSuppliersByFieldsAndDateRange(String[] fields, Instant start, Instant end) {
        Aggregation aggregation;
        ProjectionOperation projectionOperation = Aggregation.project(fields);
        if(start != null && end != null) {
            MatchOperation matchOperation = Aggregation.match(Criteria.where("createdAt").gte(start).lte(end));
            aggregation = Aggregation.newAggregation(
                    matchOperation,
                    projectionOperation
            );
        }else{
            aggregation = Aggregation.newAggregation(
                    projectionOperation
            );
        }
        AggregationResults<ExportSupplierDataResponse> results = mongoTemplate.aggregate(
                aggregation, "suppliers", ExportSupplierDataResponse.class
        );
        return results.getMappedResults();
    }
}
