package com.rin.kanban.data.form;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ProductFormItems {
    private final List<FormItem> getFormItems = Arrays.asList(
            FormItem.builder()
                    .key("title")
                    .value("title")
                    .label("Product Name")
                    .placeholder("Enter product name")
                    .type("default")
                    .required(true)
                    .message("Enter product name")
                    .displayLength(220)
                    .build(),
            FormItem.builder()
                    .key("categories")
                    .value("categories")
                    .label("Category")
                    .placeholder("Select product category")
                    .type("select")
                    .required(false)
                    .message("Select product category")
                    .displayLength(150)
                    .build(),
            FormItem.builder()
                    .key("price")
                    .value("price")
                    .label("Price")
                    .placeholder("Enter price")
                    .type("number")
                    .required(true)
                    .message("Enter price")
                    .defaultValue("")
                    .displayLength(150)
                    .build(),
            FormItem.builder()
                    .key("quantity")
                    .value("quantity")
                    .label("Quantity")
                    .placeholder("Enter contact number")
                    .type("tel")
                    .required(false)
                    .message("Enter contact number!")
                    .displayLength(150)
                    .build(),
            FormItem.builder()
                    .key("expiry-date")
                    .value("expiry-date")
                    .label("Expiry date")
                    .placeholder("")
                    .type("date")
                    .required(true)
                    .message("Select expiry date!")
                    .displayLength(200)
                    .build(),
            FormItem.builder()
                    .key("description")
                    .value("description")
                    .label("Description")
                    .placeholder("Enter description")
                    .type("text-area")
                    .required(true)
                    .message("Enter description")
                    .displayLength(150)
                    .build()
    );
}
