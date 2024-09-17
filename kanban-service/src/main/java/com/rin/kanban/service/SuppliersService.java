package com.rin.kanban.service;

import com.rin.kanban.dto.request.SupplierRequest;
import com.rin.kanban.dto.response.SupplierResponse;
import com.rin.kanban.entity.Supplier;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.SuppliersMapper;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.SuppliersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SuppliersService {

    SuppliersMapper suppliersMapper;
    SuppliersRepository suppliersRepository;
    CategoryRepository categoryRepository;

    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = suppliersMapper.toSupplier(request);
        //

        return suppliersMapper.toSupplierResponse(suppliersRepository.save(supplier));
    }

    public List<SupplierResponse> getAll() {
        return suppliersRepository.findAll().stream().map(suppliersMapper::toSupplierResponse).toList();
    }

    public SupplierResponse updateSuppliers(SupplierRequest request, String supplierId) {
        Supplier supplier = suppliersRepository.findById(supplierId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIERS_NOT_FOUND));

        suppliersMapper.supplierUpdate(supplier, request);

        return suppliersMapper.toSupplierResponse(suppliersRepository.save(supplier));
    }

    public boolean removeSupplier(String suppliersId) {
        try {
            suppliersRepository.deleteById(suppliersId);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
