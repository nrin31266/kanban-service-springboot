package com.rin.kanban.data.form;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormItem {
    String key;
    String value;
    String label;
    String placeholder;
    String type;
    boolean required;
    String message;
    String defaultValue;
    boolean checked;
}
