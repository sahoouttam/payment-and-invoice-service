package com.payment.and.invoice.service.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.and.invoice.service.dtos.request.PSPChargeRequest;
import com.payment.and.invoice.service.dtos.response.PSPChargeResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/psp")
public class MockPSPController {
    
    @PostMapping("/charge")
    public ResponseEntity<PSPChargeResponse> charge(
                    @RequestBody PSPChargeRequest pspChargeRequest) {
        String cardToken = pspChargeRequest.getCardToken();
        log.info("Mock PSP charge request - Card token: {}", cardToken);
        
        PSPChargeResponse response = new PSPChargeResponse();
        
        switch (cardToken) {
            case "tok_success":
                try { 
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response.setStatus("succeeded");
                response.setPspRef("psp_" + UUID.randomUUID().toString().substring(0, 8));
                log.info("PSP charge successful - Ref: {}", response.getPspRef());
                break;
                
            case "tok_insufficient_funds":
                try { 
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response.setStatus("failed");
                response.setCode("insufficient_funds");
                log.warn("PSP charge failed - Insufficient funds");
                break;
                
            case "tok_card_declined":
                try { 
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response.setStatus("failed");
                response.setCode("card_declined");
                log.warn("PSP charge failed - Card declined");
                break;
                
            case "tok_timeout":
                try { 
                    Thread.sleep(30000); // 30 seconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                response.setStatus("succeeded");
                response.setPspRef("psp_" + UUID.randomUUID().toString().substring(0, 8));
                log.info("PSP charge successful after timeout - Ref: {}", response.getPspRef());
                break;
                
            case "tok_network_error":
                log.error("PSP network error");
                return ResponseEntity.status(500).build();
                
            default:
                response.setStatus("failed");
                response.setCode("invalid_token");
                log.warn("PSP charge failed - Invalid token: {}", cardToken);
                break;
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}