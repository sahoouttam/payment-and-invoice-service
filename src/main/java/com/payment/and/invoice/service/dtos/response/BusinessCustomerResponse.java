package com.payment.and.invoice.service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCustomerResponse {
    
    private Long businessId;
    private String businessName;
    private Long customerId;
    private String customerName;
}
