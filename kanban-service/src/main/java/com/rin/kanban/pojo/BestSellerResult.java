package com.rin.kanban.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BestSellerResult {
    List<String> ids;
}
