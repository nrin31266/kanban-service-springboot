package com.kanban.profile.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WardResponse {
    String id;
    private String name;
    private String type;
    private String slug;
    private String nameWithType;
    private String path;
    private String pathWithType;
    private String code;
    private String parentCode; // Reference to District code
    private boolean isDeleted;
}
