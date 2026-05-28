package com.payment.and.invoice.service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.dtos.request.CreateCustomerRequest;
import com.payment.and.invoice.service.dtos.response.CustomerResponse;
import com.payment.and.invoice.service.exception.CustomerNotFoundException;
import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.model.Customer;
import com.payment.and.invoice.service.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest) {
        Customer customer = Customer.builder()
                                .name(createCustomerRequest.getName())
                                .email(createCustomerRequest.getEmail())
                                .build();
        Customer createdCustomer = customerRepository.save(customer);
        log.info("Customer created with {}", createdCustomer.getId());
        return mapToCustomerResponse(createdCustomer);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                        .orElseThrow(() -> new CustomerNotFoundException(
                            "customer is not registered"));
    }

    public CustomerResponse findCustomerById(Long customerId) {
        Customer customer = findById(customerId);
        return mapToCustomerResponse(customer);
    }

    public CustomerResponse onboardCustomer(Business business, Long customerId) {
        Customer customer = findById(customerId);
        customer.setBusiness(business);
        Customer onboardedCustomer = customerRepository.save(customer);
        log.info("Customer with id {} onboarded to business with id: {} and name: {}", 
                customerId, business.getId(), business.getName());
        return mapToCustomerResponse(onboardedCustomer);
    }

    public Customer findCustomerByIdAndBusiness(Long id, Business business) {
        return customerRepository.findByIdAndBusiness(id, business)
                        .orElseThrow(() -> new CustomerNotFoundException(
                            "customer is not found"));
    }

    public List<CustomerResponse> findAllCustomers() {
        return customerRepository.findAll()
                            .stream()
                            .map(customer -> mapToCustomerResponse(customer))
                            .collect(Collectors.toList());
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .build();
    }
}
