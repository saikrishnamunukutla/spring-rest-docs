package com.sk.springrestdocs.service;

import com.sk.springrestdocs.model.Customer;
import com.sk.springrestdocs.model.CustomerRequest;
import com.sk.springrestdocs.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CustomerService {

    private final CustomerRepository customerRepository;

    public List<Customer> getCustomers(String customerId) {
        return Objects.nonNull(customerId) ? List.of(customerRepository.findByCustomerId(customerId)) : customerRepository.findAll();
    }

    public Customer addCustomer(CustomerRequest customerRequest) {
        Customer customer = new Customer(UUID.randomUUID().toString(), customerRequest.getFirstName(), customerRequest.getLastName(), customerRequest.getEmail(), customerRequest.getPhone());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(String customerId) {
        customerRepository.deleteById(customerId);
    }
}
