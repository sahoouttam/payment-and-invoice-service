package com.payment.and.invoice.service.dtos.request;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateInvoiceRequest {
    
    private Long businessId;
    private Long customerId;
    private LocalDateTime dueDate;
    private List<LineItemRequest> itemRequests;
}
