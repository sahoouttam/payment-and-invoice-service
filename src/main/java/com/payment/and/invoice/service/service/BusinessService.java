package com.payment.and.invoice.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.dtos.request.CreateBusinessRequest;
import com.payment.and.invoice.service.dtos.response.BusinessCustomerResponse;
import com.payment.and.invoice.service.dtos.response.CreateBusinessResponse;
import com.payment.and.invoice.service.dtos.response.CustomerResponse;
import com.payment.and.invoice.service.exception.BusinessNotFoundException;
import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.repository.BusinessRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BusinessService {
    
    private final BusinessRepository businessRepository;
    private final CustomerService customerService;

    @Autowired
    public BusinessService(BusinessRepository businessRepository,
                           CustomerService customerService) {
        this.businessRepository = businessRepository;
        this.customerService = customerService;
    }

    public CreateBusinessResponse createBusiness(CreateBusinessRequest createBusinessRequest) {
        Business business = Business.builder()
                        .name(createBusinessRequest.getName())
                        .build();
        Business createdBusiness = businessRepository.save(business);
        return new CreateBusinessResponse(createdBusiness.getId(),
                                          createdBusiness.getName());
    }

    public BusinessCustomerResponse onboardCustomer(Long businessId, Long customerId) {
        Business business = findBusinessById(businessId);
        CustomerResponse customerResponse = customerService.onboardCustomer(business, customerId);
        return new BusinessCustomerResponse(
            business.getId(),
            business.getName(),
            customerResponse.getId(),
            customerResponse.getName()
        );
    }

    public Business findBusinessById(Long id) {
        return businessRepository.findById(id)
                    .orElseThrow(() -> new BusinessNotFoundException(
                        "business not found"));

    }
}
