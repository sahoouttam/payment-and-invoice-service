package com.payment.and.invoice.service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.model.WebhookEndpoint;

@Repository
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint, Long> {
    
    List<WebhookEndpoint> findByBusinessAndActive(Business business, Boolean active);

    boolean existsByBusinessAndUrl(Business business, String url);

    List<WebhookEndpoint> findByBusiness(Business business);

    Optional<WebhookEndpoint> findByIdAndBusiness(Long id, Business business);
}
