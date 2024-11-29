package com.rin.kanban.repository.custom;

import com.rin.kanban.pojo.SoldCountResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.swing.text.Document;
import java.math.BigDecimal;

@RequiredArgsConstructor
@Repository
@Slf4j

public class OrderCustomRepositoryImp implements OrderCustomRepository {
    private final MongoTemplate mongoTemplate;


    @Override
    public BigDecimal getSoldCountByProductId(String productId) {
        MatchOperation matchOrder = Aggregation.match(Criteria.where("isComplete").is(true));
        LookupOperation lookupOrderProducts = Aggregation.lookup("order-product", "_id", "orderId", "orderProducts");


        // Unwind danh sách orderProducts để xử lý từng item trong mảng
        UnwindOperation unwindOrderProducts = Aggregation.unwind("orderProducts");
        MatchOperation matchOrderProduct = Aggregation.match(Criteria.where("orderProducts.productId").is(productId));

        // Group theo productId và tính tổng số lượng đã bán (count)
        GroupOperation groupByProductId = Aggregation.group("orderProducts.productId")
                .sum("orderProducts.count").as("totalSold");
        // Build Aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
                matchOrder,            // Match các Order đã hoàn thành
                lookupOrderProducts,   // Lookup OrderProduct
                unwindOrderProducts,   // Unwind các OrderProduct
                matchOrderProduct,     // Match productId trong OrderProduct
                groupByProductId       // Tính tổng số lượng đã bán
        );

        AggregationResults<SoldCountResult> results = mongoTemplate.aggregate(aggregation, "orders", SoldCountResult.class);

        // Kiểm tra nếu có kết quả
        if (results != null && !results.getMappedResults().isEmpty()) {
            SoldCountResult result = results.getMappedResults().get(0); // Lấy kết quả đầu tiên
            if (result != null && result.getTotalSold() != null) {
                return result.getTotalSold(); // Trả về BigDecimal
            }
        }

        return BigDecimal.ZERO; // Nếu không có kết quả hoặc totalSold là null
    }
}
