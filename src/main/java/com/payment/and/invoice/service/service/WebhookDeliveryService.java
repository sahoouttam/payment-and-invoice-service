package com.payment.and.invoice.service.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.dtos.response.WebhookDeliveryResponse;
import com.payment.and.invoice.service.model.WebhookDelivery;
import com.payment.and.invoice.service.model.WebhookDeliveryStatus;
import com.payment.and.invoice.service.model.WebhookEndpoint;
import com.payment.and.invoice.service.repository.WebhookDeliveryRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WebhookDeliveryService {
    
    private final WebhookDeliveryRepository webhookDeliveryRepository;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Autowired
    public WebhookDeliveryService(WebhookDeliveryRepository webhookDeliveryRepository) {
        this.webhookDeliveryRepository = webhookDeliveryRepository;
    }

    public WebhookDelivery saveWebhookDelivery(WebhookDelivery webhookDelivery) {
        return webhookDeliveryRepository.save(webhookDelivery);
    }

    public void attemptDelivery(WebhookDelivery webhookDelivery) {
        WebhookDelivery delivery = saveWebhookDelivery(webhookDelivery);
        log.debug("Created webhook delivery {} for event {}", 
                    delivery.getId(), delivery.getWebhookEventType()); 
        
        try {
            String signature = generateSignature(webhookDelivery.getPayload());
            HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(delivery.getWebhookEndpoint().getUrl()))
                        .header("Content-type", "application/json")
                        .header("X-Webhook-Signature", signature)
                        .header("X-Webhook-Event", delivery.getWebhookEventType().getEventName())
                        .header("X-Webhook-Delivery-Id", delivery.getId().toString())
                        .POST(HttpRequest.BodyPublishers.ofString(delivery.getPayload()))
                        .build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            delivery.setAttemptCount(delivery.getAttemptCount() + 1);
            delivery.setLastResponseCode(response.statusCode());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                delivery.setWebhookDeliveryStatus(WebhookDeliveryStatus.DELIVERED);
                delivery.setDeliveredAt(LocalDateTime.now());
                log.info("Webhook delivered {} attempt {}", delivery.getId(), delivery.getAttemptCount());
            } else {
                scheduleRetry(delivery, "HTTP " + response.statusCode());
            }
        } catch (Exception e) {
            delivery.setAttemptCount(webhookDelivery.getAttemptCount() + 1);
            scheduleRetry(delivery, e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        saveWebhookDelivery(delivery);
    }

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void retryFailedDeliveries() {
        List<WebhookDelivery> deliveries = webhookDeliveryRepository
                .findByWebhookDeliveryStatusAndNextAttemptAtBefore(
                    WebhookDeliveryStatus.FAILED, LocalDateTime.now());
        
        if (!deliveries.isEmpty()) {
            log.info("Retrying {} failed webhook deliveries", deliveries.size());
            for (WebhookDelivery webhookDelivery : deliveries) {
                webhookDelivery.setWebhookDeliveryStatus(WebhookDeliveryStatus.PENDING);
                saveWebhookDelivery(webhookDelivery);
                attemptDelivery(webhookDelivery);
            }
        }
    }

    public List<WebhookDeliveryResponse> getDeliveries(List<WebhookEndpoint> webhookEndpoints) {
        return webhookDeliveryRepository.findByWebhookEndpointIn(webhookEndpoints)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void scheduleRetry(WebhookDelivery webhookDelivery, String error) {
        webhookDelivery.setLastError(error);
        if (webhookDelivery.getAttemptCount() >= 5) {
            webhookDelivery.setWebhookDeliveryStatus(WebhookDeliveryStatus.EXHAUSTED);
            log.warn("Webhook exhausted after {} attempts: {}", 5, webhookDelivery.getId());
        } else {
            webhookDelivery.setWebhookDeliveryStatus(WebhookDeliveryStatus.FAILED);
            int[] backoffMinutes = {1, 5, 15, 30, 60};
            int index = Math.max(0, webhookDelivery.getAttemptCount() - 1); 
            index = Math.min(index, backoffMinutes.length - 1);
            int delay = backoffMinutes[index];
            webhookDelivery.setNextAttemptAt(LocalDateTime.now().plusMinutes(delay));
            log.info("Webhook retry in {} min {}", delay, webhookDelivery.getId());
        }
    }

    private String generateSignature(String payload) {
        try {
            String secret = "webhook-secret-key";
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(keySpec);
            byte[] signature = mac.doFinal(payload.getBytes());
            return Base64.getEncoder().encodeToString(signature);
        } catch (Exception e) {
            log.error("Failed to generate signature", e);
            return "";
        }
    }

    private WebhookDeliveryResponse mapToResponse(WebhookDelivery webhookDelivery) {
        return WebhookDeliveryResponse.builder()
                    .id(webhookDelivery.getId())
                    .webhookEventType(webhookDelivery.getWebhookEventType().getEventName())
                    .endpointUrl(webhookDelivery.getWebhookEndpoint() == null 
                        ? null : webhookDelivery.getWebhookEndpoint().getUrl())
                    .webhookDeliveryStatus(webhookDelivery.getWebhookDeliveryStatus())
                    .attemptCount(webhookDelivery.getAttemptCount())
                    .deliveredAt(webhookDelivery.getDeliveredAt())
                    .nextAttemptAt(webhookDelivery.getNextAttemptAt())
                    .lastResponseCode(webhookDelivery.getLastResponseCode())
                    .lastError(webhookDelivery.getLastError())
                    .createdAt(webhookDelivery.getCreatedAt())
                    .build();
    }
}  
