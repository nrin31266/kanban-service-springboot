package com.rin.kanban.service;

import com.rin.kanban.data.form.SupplierFormItems;
import com.rin.kanban.dto.PageResponse;
import com.rin.kanban.data.form.FormModel;
import com.rin.kanban.dto.request.ExportDataRequest;
import com.rin.kanban.dto.request.SupplierRequest;
import com.rin.kanban.dto.response.ExportSupplierDataResponse;
import com.rin.kanban.dto.response.ProductHasSubProductsResponse;
import com.rin.kanban.dto.response.SubProductResponse;
import com.rin.kanban.dto.response.SupplierResponse;
import com.rin.kanban.entity.Product;
import com.rin.kanban.entity.SubProduct;
import com.rin.kanban.entity.Supplier;
import com.rin.kanban.exception.AppException;
import com.rin.kanban.exception.ErrorCode;
import com.rin.kanban.mapper.SuppliersMapper;
import com.rin.kanban.repository.CategoryRepository;
import com.rin.kanban.repository.SuppliersCustomRepository;
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
    CategoryRepository categoryRepository;
    SuppliersCustomRepository suppliersCustomRepository;

    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = supplierMapper.toSupplier(request);
        //
        return supplierMapper.toSupplierResponse(suppliersRepository.save(supplier));
    }



    public SupplierResponse updateSuppliers(SupplierRequest request, String supplierId) {
        Supplier supplier = suppliersRepository.findById(supplierId)
                .orElseThrow(() -> new AppException(ErrorCode.SUPPLIERS_NOT_FOUND));

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

    public FormModel getForm() {
        SupplierFormItems itemsData = new SupplierFormItems();
        return FormModel.builder()
                .title("Suppliers")
                .layout("horizontal")
                .labelCol(8)
                .wrapperCol(16)
                .formItems(itemsData.getGetFormItems())
                .build();
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

    public PageResponse<SupplierResponse> getAll() {
        List<SupplierResponse> suppliers= suppliersRepository.findAll().stream().map(supplierMapper::toSupplierResponse).toList();
        return PageResponse.<SupplierResponse>builder()
                .totalPages(1)
                .currentPage(1)
                .pageSize(suppliers.size())
                .totalElements(suppliers.size())
                .data(suppliers)
                .build();
    }

    public List<ExportSupplierDataResponse> exportSuppliersData(Instant start, Instant end, ExportDataRequest request) {
        return suppliersCustomRepository.findSuppliersByFieldsAndDateRange(request.getCheckedValue(), start, end);
    }
}
