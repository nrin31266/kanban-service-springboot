package com.rin.kanban.data.form;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormItem {
    String key;
    String value;
    String label;
    String placeholder;
    String type;
    Boolean required;
    String message;
    String defaultValue;
    Boolean checked;
    Integer displayLength;
}
