package com.rin.kanban.repository;

import com.rin.kanban.dto.response.ExportSupplierDataResponse;

import java.time.Instant;
import java.util.List;

public interface SuppliersCustomRepository {
    List<ExportSupplierDataResponse> findSuppliersByFieldsAndDateRange(String[] fields, Instant start, Instant end);
}
