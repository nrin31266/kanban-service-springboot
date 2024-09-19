package com.rin.kanban.service;

import com.rin.kanban.data.form.FormItemsData;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.data.form.FormItem;
import com.rin.kanban.data.form.FormModel;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SuppliersService {

    SuppliersMapper suppliersMapper;
    SuppliersRepository suppliersRepository;
    CategoryRepository categoryRepository;

    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = suppliersMapper.toSupplier(request);
        //
        return suppliersMapper.toSupplierResponse(suppliersRepository.save(supplier));
    }

    public PageResponse<SupplierResponse> getAll(int page, int size) {
        Sort sort = Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Supplier> pageData = suppliersRepository.findAll(pageable);

        return PageResponse.<SupplierResponse>builder()
                .currentPage(pageData.getNumber() + 1)
                .totalPages(pageData.getTotalPages())
                .pageSize(pageData.getSize())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent().stream().map(suppliersMapper::toSupplierResponse).toList())
                .build();
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
        } catch (Exception e) {
            return false;
        }
    }

    public FormModel getForm() {
        FormItemsData itemsData = new FormItemsData();
        return FormModel.builder()
                .title("Suppliers")
                .layout("horizontal")
                .labelCol(8)
                .wrapperCol(16)
                .formItems(itemsData.getGetFormItems())
                .build();
    }
}
