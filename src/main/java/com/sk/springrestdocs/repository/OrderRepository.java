package com.sk.springrestdocs.repository;

import com.sk.springrestdocs.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderRepository extends MongoRepository<Order, String>{

    Order findByOrderId(String orderId);
    Order findByCustomerId(String customerId);
}
