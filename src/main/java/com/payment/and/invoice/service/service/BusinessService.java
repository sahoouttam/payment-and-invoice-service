package com.payment.and.invoice.service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.dtos.request.CreateBusinessRequest;
import com.payment.and.invoice.service.dtos.response.CreateBusinessResponse;
import com.payment.and.invoice.service.exception.NotFoundException;
import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.repository.BusinessRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BusinessService {
    
    private final BusinessRepository businessRepository;

    @Autowired
    public BusinessService(BusinessRepository businessRepository) {
        this.businessRepository = businessRepository;
    }

    public CreateBusinessResponse createBusiness(CreateBusinessRequest createBusinessRequest) {
        Business business = Business.builder()
                        .name(createBusinessRequest.getName())
                        .build();
        Business createdBusiness = businessRepository.save(business);
        return new CreateBusinessResponse(createdBusiness.getId(),
                                          createdBusiness.getName());
    }

    public Business findBusinessById(Long id) {
        return businessRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException(
                        "business not found"));

    }
}
