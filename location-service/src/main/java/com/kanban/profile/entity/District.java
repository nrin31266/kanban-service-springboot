package com.kanban.profile.entity;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder
@AllArgsConstructor
@Data
@Document("districts")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class District {
    @MongoId
    String id;
    private String name;
    private String type;
    private String slug;
    @Field("name_with_type")
    private String nameWithType;
    private String path;
    @Field("path_with_type")
    private String pathWithType;
    private String code;
    @Field("parent_code")
    private String parentCode; // Reference to Province code
    private boolean isDeleted;
}
