package com.payment.and.invoice.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.and.invoice.service.dtos.request.CreateBusinessRequest;
import com.payment.and.invoice.service.dtos.response.CreateBusinessResponse;
import com.payment.and.invoice.service.service.BusinessService;

@RestController
@RequestMapping("/api/v1/businesses")
public class BusinessController {
    
    private final BusinessService businessService;

    @Autowired
    public BusinessController(BusinessService businessService) {
        this.businessService = businessService;
    }

    @PostMapping
    public ResponseEntity<CreateBusinessResponse> createBusiness(
                @RequestBody CreateBusinessRequest createBusinessRequest) {
        CreateBusinessResponse createBusinessResponse = businessService
                                        .createBusiness(createBusinessRequest);
        return new ResponseEntity<>(createBusinessResponse, HttpStatus.CREATED);
    }
}
