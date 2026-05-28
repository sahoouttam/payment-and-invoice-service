package com.payment.and.invoice.service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.model.WebhookEndpoint;

@Repository
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint, Long> {
    
    Optional<WebhookEndpoint> findByBusinessAndActive(Business business, Boolean active);
}
