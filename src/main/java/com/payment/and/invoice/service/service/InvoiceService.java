package com.payment.and.invoice.service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.dtos.WebhookData;
import com.payment.and.invoice.service.dtos.WebhookPayload;
import com.payment.and.invoice.service.dtos.request.CreateInvoiceRequest;
import com.payment.and.invoice.service.dtos.request.LineItemRequest;
import com.payment.and.invoice.service.dtos.request.PSPChargeRequest;
import com.payment.and.invoice.service.dtos.request.PaymentRequest;
import com.payment.and.invoice.service.dtos.response.CreateInvoiceResponse;
import com.payment.and.invoice.service.dtos.response.InvoicePaymentResponse;
import com.payment.and.invoice.service.dtos.response.LineItemResponse;
import com.payment.and.invoice.service.dtos.response.PaymentAttemptResponse;
import com.payment.and.invoice.service.exception.NotFoundException;
import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.model.Customer;
import com.payment.and.invoice.service.model.Invoice;
import com.payment.and.invoice.service.model.InvoiceStatus;
import com.payment.and.invoice.service.model.PaymentAttemptStatus;
import com.payment.and.invoice.service.model.WebhookEventType;
import com.payment.and.invoice.service.repository.InvoiceRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final BusinessService businessService;
    private final CustomerService customerService;
    private final LineItemService lineItemService;
    private final PaymentAttemptService paymentAttemptService;
    private final WebhookEndpointService webhookEndpointService;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, BusinessService businessService,
            CustomerService customerService, LineItemService lineItemService,
            PaymentAttemptService paymentAttemptService, 
            WebhookEndpointService webhookEndpointService) {
        this.invoiceRepository = invoiceRepository;
        this.businessService = businessService;
        this.customerService = customerService;
        this.lineItemService = lineItemService;
        this.paymentAttemptService = paymentAttemptService;
        this.webhookEndpointService = webhookEndpointService;
    }

    public CreateInvoiceResponse createInvoice(Long businessId, 
                            CreateInvoiceRequest createInvoiceRequest) {
        Business business = businessService.findBusinessById(businessId);
        Customer customer = customerService.findCustomerByIdAndBusiness(
                                    createInvoiceRequest.getCustomerId(), business); 
        Invoice invoice = Invoice.builder()
                                .business(business)
                                .customer(customer)
                                .invoiceStatus(InvoiceStatus.OPEN)
                                .totalCents(computeTotal(createInvoiceRequest.getItemRequests()))
                                .dueDate(LocalDateTime.now().plusDays(30))
                                .build();
        Invoice savedInvoice = invoiceRepository.save(invoice);
        List<LineItemResponse> lineItemResponses = lineItemService.saveAllLineItem(
                        createInvoiceRequest.getItemRequests(), savedInvoice);
        webhookEndpointService.sendWebhook(createWebhookPayload(invoice, WebhookEventType.INVOICE_CREATED));
        return CreateInvoiceResponse.builder()
                            .invoiceId(invoice.getId())
                            .invoiceStatus(invoice.getInvoiceStatus())
                            .businessId(business.getId())
                            .businessName(business.getName())
                            .customerId(customer.getId())
                            .customerName(customer.getName())
                            .totalCents(invoice.getTotalCents())
                            .dueDate(invoice.getDueDate())
                            .lineItemResponses(lineItemResponses)
                            .build();
    }

    public InvoicePaymentResponse processPayment(Long invoiceId, 
                                         String idempotencyKey,
                                         PaymentRequest paymentRequest,
                                         Long businessId) {
        Business business = businessService.findBusinessById(businessId);
        Invoice invoice = invoiceRepository.findByIdAndBusiness(invoiceId, business)
                                    .orElseThrow(() -> new NotFoundException(
                                        "invoice not found"));
        if (invoice.getInvoiceStatus() != InvoiceStatus.OPEN) {
            throw new IllegalStateException(
                "Invoice is not in payable state. Current: " + invoice.getInvoiceStatus());
        }
        PaymentAttemptResponse paymentAttemptResponse = paymentAttemptService
                        .processPaymentAttempt(
                            invoice, 
                            idempotencyKey,
                            new PSPChargeRequest(paymentRequest.getCardToken(), invoice.getTotalCents()));
        if (paymentAttemptResponse.getPaymentAttemptStatus() == PaymentAttemptStatus.SUCCESS) {
            invoice.setInvoiceStatus(InvoiceStatus.PAID);
            webhookEndpointService.sendWebhook(createWebhookPayload(invoice, WebhookEventType.INVOICE_PAID));
            log.info("Invoice {} marked as PAID", invoice.getId());
        } else if (paymentAttemptResponse.getPaymentAttemptStatus() == PaymentAttemptStatus.FAILED) {
            webhookEndpointService.sendWebhook(createWebhookPayload(invoice, WebhookEventType.INVOICE_PAYMENT_FAILED));
            log.info("Payment processing for Invoice {} FAILED", invoice.getId());
        }
        Invoice savedInvoice = invoiceRepository.save(invoice);
        return mapToResponse(savedInvoice, paymentAttemptResponse);
    }
        
    private Integer computeTotal(List<LineItemRequest> itemRequests) {
        return itemRequests.stream()
                    .map(item -> item.getQuantity() * item.getUnitAmountCents())
                    .reduce(0, Integer::sum);
    }

    private InvoicePaymentResponse mapToResponse(Invoice invoice, 
                        PaymentAttemptResponse payAttemptResponse) {
        return InvoicePaymentResponse.builder()
                    .invoiceId(invoice.getId())
                    .invoiceStatus(invoice.getInvoiceStatus())
                    .paymentAttemptId(payAttemptResponse.getId())
                    .totalCents(invoice.getTotalCents())
                    .pspReference(payAttemptResponse.getPspReference())
                    .errorMessage(payAttemptResponse.getErrorMessage())
                    .build();
    }

    private WebhookPayload createWebhookPayload(Invoice invoice, WebhookEventType eventType) {
        WebhookData webhookData = WebhookData.builder()
                        .invoiceId(invoice.getId())
                        .businessId(invoice.getBusiness().getId())
                        .customerId(invoice.getCustomer() != null ? invoice.getCustomer().getId() : null)
                        .invoiceStatus(invoice.getInvoiceStatus().toString())
                        .totalCents(invoice.getTotalCents())
                        .dueDate(invoice.getDueDate() != null ? invoice.getDueDate().toString() : null)
                        .build();
        return WebhookPayload.builder()
                        .webhookEventType(eventType)
                        .timestamp(LocalDateTime.now().toString())
                        .webhookData(webhookData)
                        .build();
    }
}
