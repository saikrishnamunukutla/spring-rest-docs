package com.sk.springrestdocs.controller;

import com.sk.springrestdocs.model.Customer;
import com.sk.springrestdocs.model.CustomerRequest;
import com.sk.springrestdocs.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getCustomers(@RequestHeader(name = "X-AUTH-TOKEN") String xAuthToken, @RequestParam(required = false) String customerId) {
        return ResponseEntity.ok(customerService.getCustomers(customerId));
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> addCustomer(@RequestHeader(name = "X-AUTH-TOKEN") String xAuthToken,@RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.addCustomer(customerRequest));
    }

    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<Object> deleteCustomer(@RequestHeader(name = "X-AUTH-TOKEN") String xAuthToken, @PathVariable(name = "customerId") String customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.noContent().build();
    }

}
