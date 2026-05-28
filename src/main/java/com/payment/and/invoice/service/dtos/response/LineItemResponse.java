package com.payment.and.invoice.service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LineItemResponse {
    
    private Long lineItemId;
    private String description;
    private Integer quantity;
    private Integer unitAmountCents;
}
