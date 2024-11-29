package com.kanban.profile.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Data
@Document("provinces")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Province {
    @MongoId
    String id;
    String name;
    String slug;
    String type;
    @Field("name_with_type")
    String nameWithType;
    String code;
    boolean isDeleted;
}
