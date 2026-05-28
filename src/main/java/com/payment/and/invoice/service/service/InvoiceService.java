package com.payment.and.invoice.service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.dtos.request.CreateInvoiceRequest;
import com.payment.and.invoice.service.dtos.request.PaymentRequest;
import com.payment.and.invoice.service.dtos.response.CreateInvoiceResponse;
import com.payment.and.invoice.service.dtos.response.LineItemResponse;
import com.payment.and.invoice.service.dtos.response.PaymentResponse;
import com.payment.and.invoice.service.model.Business;
import com.payment.and.invoice.service.model.Customer;
import com.payment.and.invoice.service.model.Invoice;
import com.payment.and.invoice.service.model.InvoiceStatus;
import com.payment.and.invoice.service.repository.InvoiceRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final BusinessService businessService;
    private final CustomerService customerService;
    private final LineItemService lineItemService;

    @Autowired
    public InvoiceService(InvoiceRepository invoiceRepository, BusinessService businessService,
            CustomerService customerService, LineItemService lineItemService) {
        this.invoiceRepository = invoiceRepository;
        this.businessService = businessService;
        this.customerService = customerService;
        this.lineItemService = lineItemService;
    }

    public CreateInvoiceResponse createInvoice(CreateInvoiceRequest createInvoiceRequest) {
        Business business = businessService.findBusinessById(createInvoiceRequest.getBusinessId());
        Customer customer = customerService.findCustomerByIdAndBusiness(
                                    createInvoiceRequest.getCustomerId(), business); 
        Invoice invoice = Invoice.builder()
                                .business(business)
                                .customer(customer)
                                .invoiceStatus(InvoiceStatus.DRAFT)
                                .dueDate(LocalDateTime.now())
                                .build();
        Invoice savedInvoice = invoiceRepository.save(invoice);
        
        List<LineItemResponse> lineItemResponses = lineItemService.saveAllLineItem(
                        createInvoiceRequest.getItemRequests(), savedInvoice);
        Integer totalCents = lineItemResponses.stream()
                                            .map(response -> response.getQuantity() * response.getUnitAmountCents())
                                            .reduce(0, Integer::sum);
        savedInvoice.setTotalCents(totalCents);
        invoiceRepository.save(savedInvoice);
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

    /*public PaymentResponse processPayment(String invoiceId, 
                                         String idempotencyKey,
                                         PaymentRequest request,
                                         Long businessId) {
        Business business = businessService.findBusinessById(businessId);                                    
    }*/
}
