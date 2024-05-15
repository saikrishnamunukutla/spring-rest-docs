package com.sk.springrestdocs.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
