package com.rin.kanban.service;

import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.dto.request.ExportDataRequest;
import com.rin.kanban.dto.request.SupplierRequest;
import com.rin.kanban.dto.response.ExportSupplierDataResponse;
import com.rin.kanban.dto.response.SupplierResponse;
import com.rin.kanban.entity.Supplier;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.SuppliersMapper;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.custom.SuppliersCustomRepository;
import com.rin.kanban.repository.SuppliersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SuppliersService {

    SuppliersMapper supplierMapper;
    SuppliersRepository suppliersRepository;
    SuppliersCustomRepository suppliersCustomRepository;

    public SupplierResponse create(SupplierRequest request) {
        if(suppliersRepository.findByName(request.getName()).isPresent()){
            throw new AppException(ErrorCode.SUPPLIERS_EXISTS);
        }
        Supplier supplier = supplierMapper.toSupplier(request);

        return supplierMapper.toSupplierResponse(suppliersRepository.save(supplier));
    }


    public SupplierResponse updateSuppliers(SupplierRequest request, String supplierId) {
        Supplier supplier = suppliersRepository.findById(supplierId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIERS_NOT_FOUND));
        if(suppliersRepository.findByName(request.getName()).isPresent()){
            throw new AppException(ErrorCode.SUPPLIERS_EXISTS);
        }
        supplierMapper.supplierUpdate(supplier, request);

        return supplierMapper.toSupplierResponse(suppliersRepository.save(supplier));
    }

    public boolean removeSupplier(String suppliersId) {
        try {
            suppliersRepository.deleteById(suppliersId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }



    public PageResponse<SupplierResponse> getSuppliersWithPageAndSize(int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Supplier> pageData = suppliersRepository.findAll(pageable);

        return PageResponse.<SupplierResponse>builder()
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(supplierMapper::toSupplierResponse).toList())
                .build();
    }


    public List<ExportSupplierDataResponse> exportSuppliersData(Instant start, Instant end, ExportDataRequest request) {
        return suppliersCustomRepository.findSuppliersByFieldsAndDateRange(request.getCheckedValue(), start, end);
    }
}
