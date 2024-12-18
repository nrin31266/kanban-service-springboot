package com.kanban.profile.service;

import com.kanban.event.dto.UpdateAddressIsDefaultEvent;
import com.kanban.profile.dto.request.AddressRequest;
import com.kanban.profile.dto.response.AddressResponse;
import com.kanban.profile.entity.Address;
import com.kanban.profile.exception.AppException;
import com.kanban.profile.exception.ErrorCode;
import com.kanban.profile.mapper.AddressMapper;
import com.kanban.profile.repository.AddressRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AddressService {
    AddressRepository addressRepository;
    AddressMapper addressMapper;
    KafkaTemplate<String, Object> kafkaTemplate;

    public AddressResponse createAddress(AddressRequest addressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();


        Address address = addressMapper.toAddress(addressRequest);
        address.setUserId(userId);
        addressRepository.save(address);
        AddressResponse addressResponse = addressMapper.toAddressResponse(address);
        addressResponse.setAddress(createAddress(address));
        if (address.getIsDefault()) {
            kafkaTemplate.send("update-address-is-default", UpdateAddressIsDefaultEvent.builder()
                    .address(addressResponse)
                    .userId(userId)
                    .build());
        }
        return addressResponse;

    }

    public List<AddressResponse> getAddressesByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        List<Address> addresses = addressRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return addresses.stream().map(address -> {
            AddressResponse addressResponse = addressMapper.toAddressResponse(address);
            addressResponse.setAddress(createAddress(address));
            return addressResponse;
        }).toList();

    }

    private String createAddress(Address address) {
        String addressString = "";
        if (address.getHouseNo() != null) {
            addressString += address.getHouseNo();
        }
        if (addressString.isEmpty()) {
            addressString += address.getWard();
        } else {
            addressString += ", " + address.getWard();
        }
        addressString += ", " + address.getDistrict();
        addressString += ", " + address.getProvince();
        return addressString;
    }

    public void updateAddressIsDefault(UpdateAddressIsDefaultEvent addressIsDefaultEvent) {

        List<Address> addresses = addressRepository.findByUserIdAndIsDefaultIsTrue(addressIsDefaultEvent.getUserId());
        addresses.stream().map((v) -> {
            if (v.getIsDefault() && !v.getId().equals(addressIsDefaultEvent.getAddress().getId())) {
                v.setIsDefault(false);
            }
            return v;
        }).collect(Collectors.toList());
        addressRepository.saveAll(addresses);
    }

    public void deleteAddress(String addressId) {
        addressRepository.deleteById(addressId);
    }

    public AddressResponse updateAddress(AddressRequest addressRequest, String addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        Address address = addressRepository.findById(addressId).orElseThrow(()->new AppException(ErrorCode.ADDRESS_NOT_FOUND));
        if(!address.getUserId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        addressMapper.toUpdateAddress(address, addressRequest);
        addressRepository.save(address);
        AddressResponse addressResponse = addressMapper.toAddressResponse(address);
        addressResponse.setAddress(createAddress(address));
        if (address.getIsDefault()) {
            kafkaTemplate.send("update-address-is-default", UpdateAddressIsDefaultEvent.builder()
                    .address(addressResponse)
                    .userId(userId)
                    .build());
        }
        return addressResponse;
    }



}
