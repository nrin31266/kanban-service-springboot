package com.rin.kanban.data.form;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FormModel {
    private String title;
    private String layout;
    private int labelCol;
    private int wrapperCol;
    private List<FormItem> formItems;
}
