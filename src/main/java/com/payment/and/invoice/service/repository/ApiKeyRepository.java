package com.payment.and.invoice.service.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.and.invoice.service.model.ApiKey;
import com.payment.and.invoice.service.model.Business;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    Optional<ApiKey> findByKeyHashAndRevokedFalse(String keyHash);
    
    List<ApiKey> findByBusinessAndRevokedFalse(Business business);

    Optional<ApiKey> findByIdAndRevoked(Long id, boolean revoked);
}
