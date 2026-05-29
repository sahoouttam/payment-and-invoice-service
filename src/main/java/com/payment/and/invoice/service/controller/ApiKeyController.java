package com.payment.and.invoice.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.and.invoice.service.dtos.request.GenerateApiKeyRequest;
import com.payment.and.invoice.service.dtos.response.ApiKeyResponse;
import com.payment.and.invoice.service.dtos.response.GenerateApiKeyResponse;
import com.payment.and.invoice.service.service.ApiKeyService;

@RestController
@RequestMapping("/api/v1/keys")
public class ApiKeyController {
    
    private final ApiKeyService apiKeyService;

    @Autowired
    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/generate")
    public ResponseEntity<GenerateApiKeyResponse> generateApiKey(
            @RequestBody GenerateApiKeyRequest request) {
        GenerateApiKeyResponse generateApiKeyResponse = apiKeyService.generateApiKey(request);
        return new ResponseEntity<>(generateApiKeyResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{keyId}")
    public ResponseEntity<ApiKeyResponse> getApiKey(@PathVariable Long keyId) {
        ApiKeyResponse apiKeyResponse = apiKeyService.getApiKeyById(keyId);
        return new ResponseEntity<>(apiKeyResponse, HttpStatus.OK);
    }
}
