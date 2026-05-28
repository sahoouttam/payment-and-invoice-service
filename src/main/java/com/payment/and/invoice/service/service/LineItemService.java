package com.payment.and.invoice.service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.and.invoice.service.dtos.request.LineItemRequest;
import com.payment.and.invoice.service.dtos.response.LineItemResponse;
import com.payment.and.invoice.service.model.Invoice;
import com.payment.and.invoice.service.model.LineItem;
import com.payment.and.invoice.service.repository.LineItemRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LineItemService {
    
    private final LineItemRepository lineItemRepository;

    @Autowired
    public LineItemService(LineItemRepository lineItemRepository) {
        this.lineItemRepository = lineItemRepository;
    }

    public List<LineItemResponse> saveAllLineItem(List<LineItemRequest> requests, Invoice invoice) {
        List<LineItem> lineItems = requests.stream()
                                        .map(request -> mapToLineItem(request, invoice))
                                        .collect(Collectors.toList());
        List<LineItem> savedLineItems = lineItemRepository.saveAll(lineItems);
        return savedLineItems.stream()
                            .map(lineItem -> mapToLineItemResponse(lineItem))
                            .collect(Collectors.toList());
    }

    private LineItem mapToLineItem(LineItemRequest request, Invoice invoice) {
        return LineItem.builder()
                    .invoice(invoice)
                    .description(request.getDescription())
                    .quantity(request.getQuantity())
                    .unitAmountCents(request.getUnitAmountCents())
                    .build();
    }

    public LineItemResponse mapToLineItemResponse(LineItem lineItem) {
        return LineItemResponse.builder()
                            .lineItemId(lineItem.getId())
                            .description(lineItem.getDescription())
                            .quantity(lineItem.getQuantity())
                            .unitAmountCents(lineItem.getUnitAmountCents())
                            .build();
    }
}
