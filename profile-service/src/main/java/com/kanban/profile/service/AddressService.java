package com.kanban.profile.service;

import com.kanban.profile.dto.request.AddressRequest;
import com.kanban.profile.dto.response.AddressResponse;
import com.kanban.profile.entity.Address;
import com.kanban.profile.mapper.AddressMapper;
import com.kanban.profile.repository.AddressRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AddressService {
    AddressRepository addressRepository;
    AddressMapper addressMapper;

    public AddressResponse createAddress(AddressRequest addressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();


        Address address = addressMapper.toAddress(addressRequest);
        address.setUserId(userId);
        addressRepository.save(address);
        AddressResponse addressResponse = new AddressResponse();
        addressResponse.setAddress(createAddress(address));

        return addressResponse;

    }

    public List<AddressResponse> getAddressesByUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();


        List<Address> addresses = addressRepository.findByUserId(userId);


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


}
