package com.rin.kanban.service;

import com.rin.kanban.dto.request.SuppliersRequest;
import com.rin.kanban.dto.response.SuppliersResponse;
import com.rin.kanban.entity.Suppliers;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.SuppliersMapper;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.SuppliersRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SuppliersService {

    SuppliersMapper suppliersMapper;
    SuppliersRepository suppliersRepository;
    CategoryRepository categoryRepository;

    public SuppliersResponse create(SuppliersRequest request) {
        Suppliers suppliers= suppliersMapper.toSuppliers(request);
        //
        if(suppliersRepository.findByContact(suppliers.getContact()).isPresent()){
            throw new AppException(ErrorCode.SUPPLIERS_EXISTS);
        }

        return suppliersMapper.toSuppliersResponse(suppliersRepository.save(suppliers));
    }
}
