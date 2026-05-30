package com.payment.and.invoice.service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.and.invoice.service.model.WebhookDelivery;
import com.payment.and.invoice.service.model.WebhookDeliveryStatus;
import com.payment.and.invoice.service.model.WebhookEndpoint;

@Repository
public interface WebhookDeliveryRepository extends JpaRepository<WebhookDelivery, Long> {
    
    List<WebhookDelivery> findByWebhookDeliveryStatusAndNextAttemptAtBefore(
        WebhookDeliveryStatus webhookDeliveryStatus,
        LocalDateTime nextAttemptAt
    );

    List<WebhookDelivery> findByWebhookEndpointIn(List<WebhookEndpoint> webhookEndpoints);
}
