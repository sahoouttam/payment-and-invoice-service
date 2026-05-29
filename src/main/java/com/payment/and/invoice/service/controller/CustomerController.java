package com.payment.and.invoice.service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.and.invoice.service.dtos.request.CreateCustomerRequest;
import com.payment.and.invoice.service.dtos.response.CustomerResponse;
import com.payment.and.invoice.service.security.AuthenticationUtils;
import com.payment.and.invoice.service.service.CustomerService;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    
    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        Long businessId = AuthenticationUtils.getBusinessId();
        CustomerResponse customerResponse = customerService.createCustomer(businessId,
                                                            createCustomerRequest);
        return new ResponseEntity<>(customerResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable Long id) {
        Long businessId = AuthenticationUtils.getBusinessId();
        CustomerResponse customerResponse = customerService
                                                .findCustomerById(businessId, id);
        return new ResponseEntity<>(customerResponse, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        Long businessId = AuthenticationUtils.getBusinessId();
        List<CustomerResponse> customerResponses = customerService.findAllCustomers(businessId);
        return new ResponseEntity<>(customerResponses, HttpStatus.OK);
    }
}
