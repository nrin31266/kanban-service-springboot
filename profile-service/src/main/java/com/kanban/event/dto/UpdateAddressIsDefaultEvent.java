package com.kanban.event.dto;

import com.kanban.profile.dto.response.AddressResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAddressIsDefaultEvent {
    AddressResponse address;
    String userId;
}
