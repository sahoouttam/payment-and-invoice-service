package com.payment.and.invoice.service.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.exception.ApiKeyNotFoundException;
import com.payment.and.invoice.service.model.ApiKey;
import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.repository.ApiKeyRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ApiKeyService {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String KEY_PREFIX = "sk_live_";
 
    private final ApiKeyRepository apiKeyRepository;
    private final BusinessService businessService;

    @Autowired
    public ApiKeyService(ApiKeyRepository apiKeyRepository, BusinessService businessService) {
        this.apiKeyRepository = apiKeyRepository;
        this.businessService = businessService;
    }

    public String generateApiKey(Long businessId, String keyName) {
        Business business = businessService.findBusinessById(businessId);

        byte[] keyBytes = new byte[32];
        secureRandom.nextBytes(keyBytes);
        String rawKey = Base64.getUrlEncoder().withoutPadding().encodeToString(keyBytes);
        String fullKey = "billing_" + rawKey;
        String prefix = rawKey.substring(0, 8);
        String hash = hashApiKey(fullKey);

        ApiKey apiKey = ApiKey.builder()
                            .business(business)
                            .name(keyName)
                            .keyHash(hash)
                            .keyPrefix(prefix)
                            .revoked(false)
                            .build();
        apiKeyRepository.save(apiKey);
        log.info("Generated API key '{}' for business: {}", 
                    keyName, business.getName());
        
        return fullKey;
    }

    public Business validateApiKey(String key) {
        String keyHash = hashApiKey(key);
        ApiKey apiKey = apiKeyRepository.findByKeyHashAndRevokedFalse(keyHash)
                                    .orElseThrow(() -> new ApiKeyNotFoundException(
                                        "Api key not found"));
        apiKey.setLastUsedAt(LocalDateTime.now());
        ApiKey savedApiKey = apiKeyRepository.save(apiKey);
        return savedApiKey.getBusiness();
    }

    public void revokeApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findByIdAndRevoked(id, false)
                                    .orElseThrow(() -> new ApiKeyNotFoundException(
                                        "Api key not found"));
        apiKey.setRevoked(true);
        apiKey.setRevokedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
    }

    public void restoreApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findByIdAndRevoked(id, true)
                                    .orElseThrow(() -> new ApiKeyNotFoundException(
                                        "Api key not found"));
        apiKey.setRevoked(false);
        apiKey.setRevokedAt(null);
        apiKeyRepository.save(apiKey);
    }
    
    private String hashApiKey(String apiKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(apiKey.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash API key", e);
        }
    }
}
