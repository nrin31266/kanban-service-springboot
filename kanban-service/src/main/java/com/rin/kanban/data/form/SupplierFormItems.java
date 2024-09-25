package com.rin.kanban.data.form;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
@Getter
public class SupplierFormItems {
    private final List<FormItem> getFormItems = Arrays.asList(
            FormItem.builder()
                    .key("name")
                    .value("name")
                    .label("Supplier Name")
                    .placeholder("Enter supplier")
                    .type("default")
                    .required(true)
                    .message("Enter supplier name")
                    .displayLength(220)
                    .build(),
            FormItem.builder()
                    .key("product")
                    .value("product")
                    .label("Product Name")
                    .placeholder("Enter product")
                    .type("default")
                    .required(true)
                    .message("Enter product Name")
                    .displayLength(150)
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
                    .label("Buying Price")
                    .placeholder("Enter buying price")
                    .type("number")
                    .required(false)
                    .message("Enter buying price")
                    .defaultValue("0")
                    .displayLength(150)
                    .build(),
            FormItem.builder()
                    .key("contact")
                    .value("contact")
                    .label("Contact Number")
                    .placeholder("Enter contact number")
                    .type("tel")
                    .required(false)
                    .message("Enter contact number!")
                    .displayLength(150)
                    .build(),
            FormItem.builder()
                    .key("email")
                    .value("email")
                    .label("Email")
                    .placeholder("Enter email")
                    .type("email")
                    .required(true)
                    .message("Enter email!")
                    .displayLength(200)
                    .build(),
            FormItem.builder()
                    .key("onTheWay")
                    .value("onTheWay")
                    .label("On The Way")
                    .placeholder("")
                    .type("number")
                    .required(false)
                    .message("")
                    .displayLength(120)
                    .build(),
            FormItem.builder()
                    .key("talking")
                    .value("talking")
                    .label("Talking")
                    .type("check-box")
                    .displayLength(70)
                    .message("")
                    .build()
    );
}
