package com.kanban.profile.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddressRequest {
    String houseNo;
    String province;
    String district;
    String ward;
    String name;
    String phoneNumber;
    Boolean isDefault;
}
