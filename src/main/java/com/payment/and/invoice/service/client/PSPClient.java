package com.payment.and.invoice.service.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.payment.and.invoice.service.config.PSPConfig;
import com.payment.and.invoice.service.dtos.request.PSPChargeRequest;
import com.payment.and.invoice.service.dtos.response.PSPChargeResponse;
import com.payment.and.invoice.service.exception.PSPException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PSPClient {
    
    private RestTemplate restTemplate;
    private PSPConfig pspConfig;

    public PSPClient(PSPConfig pspConfig) {   
        this.pspConfig = pspConfig;
        this.restTemplate = new RestTemplateBuilder()
                .connectTimeout(Duration.ofMillis(pspConfig.getConnectTimeoutMs()))
                .readTimeout(Duration.ofMillis(pspConfig.getReadTimeoutMs()))
                .build();
    }
    public PSPChargeResponse charge(PSPChargeRequest pspChargeRequest) {
        log.info("Initiating PSP charge for card token: {}, amount: {} cents", 
                 maskCardToken(pspChargeRequest.getCardToken()), 
                 pspChargeRequest.getAmountCents());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<PSPChargeRequest> requestEntity = new HttpEntity<>(pspChargeRequest, headers);

            log.debug("Sending PSP charge request to: {}", pspConfig.getChargeEndpoint());
            ResponseEntity<PSPChargeResponse> responseEntity = restTemplate.exchange(
                                            pspConfig.getChargeEndpoint(),
                                            HttpMethod.POST,
                                            requestEntity,
                                            PSPChargeResponse.class);
            PSPChargeResponse pspChargeResponse = responseEntity.getBody();
            if (pspChargeResponse != null && pspChargeResponse.isSuccess()) {
                log.info("PSP charge successful. PSP Reference: {}", pspChargeResponse.getPspRef());
            } else if (pspChargeResponse != null && pspChargeResponse.isFailed()) {
                log.warn("PSP charge failed. Error code: {}", pspChargeResponse.getCode());
            }
            return pspChargeResponse;
        } catch (Exception e) {
            log.error("Unexpected error processing PSP charge for card token: {}", 
                     maskCardToken(pspChargeRequest.getCardToken()), e);
            throw new PSPException("Payment processing failed due to an unexpected error.", e);
        }
    }

    private String maskCardToken(String cardToken) {
        if (cardToken == null || cardToken.length() <= 4) {
            return "****";
        }
        return "****" + cardToken.substring(cardToken.length() - 4);
    }
}
