package com.payment.and.invoice.service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.dtos.request.CreateCustomerRequest;
import com.payment.and.invoice.service.dtos.response.CustomerResponse;
import com.payment.and.invoice.service.exception.NotFoundException;
import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.model.Customer;
import com.payment.and.invoice.service.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final BusinessService businessService;

    @Autowired
    public CustomerService(CustomerRepository customerRepository,
                           BusinessService businessService) {
        this.customerRepository = customerRepository;
        this.businessService = businessService;
    }

    public CustomerResponse createCustomer(Long businessId, 
                            CreateCustomerRequest createCustomerRequest) {
        Business business = businessService.findBusinessById(businessId);                        
        Customer customer = Customer.builder()
                                .business(business)
                                .name(createCustomerRequest.getName())
                                .email(createCustomerRequest.getEmail())
                                .build();
        Customer createdCustomer = customerRepository.save(customer);
        log.info("Customer created with {}", createdCustomer.getId());
        return mapToCustomerResponse(createdCustomer);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException(
                            "customer is not registered"));
    }

    public CustomerResponse findCustomerById(Long businessId, Long customerId) {
        Business business = businessService.findBusinessById(businessId);  
        Customer customer = findCustomerByIdAndBusiness(customerId, business);
        return mapToCustomerResponse(customer);
    }

    public Customer findCustomerByIdAndBusiness(Long customerId, Business business) {
        return customerRepository.findByIdAndBusiness(customerId, business)
                                    .orElseThrow(() -> new NotFoundException(
                                    "customer is not registered"));        
    }

    public List<CustomerResponse> findAllCustomers(Long businessId) {
        Business business = businessService.findBusinessById(businessId); 
        return customerRepository.findByBusiness(business)
                            .stream()
                            .map(customer -> mapToCustomerResponse(customer))
                            .collect(Collectors.toList());
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                    .id(customer.getId())
                    .name(customer.getName())
                    .email(customer.getEmail())
                    .build();
    }
}
