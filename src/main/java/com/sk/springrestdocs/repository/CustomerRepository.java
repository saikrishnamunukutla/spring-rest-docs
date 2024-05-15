package com.sk.springrestdocs.repository;

import com.sk.springrestdocs.model.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends MongoRepository<Customer, String>{

    Customer findByCustomerId(String customerId);
}
