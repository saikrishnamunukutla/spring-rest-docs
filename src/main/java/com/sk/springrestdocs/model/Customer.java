package com.sk.springrestdocs.model;

import org.springframework.data.annotation.Id;

public record Customer(@Id String customerId, String firstName, String lastName, String email, String phone) {

}
