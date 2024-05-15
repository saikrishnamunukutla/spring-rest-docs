package com.sk.springrestdocs.model;

import org.springframework.data.annotation.Id;

public record Order(@Id String orderId, String orderName, String orderDescription, String orderStatus, String orderDate, String orderAmount, String customerId) {
}
