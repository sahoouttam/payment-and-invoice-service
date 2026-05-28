package com.payment.and.invoice.service.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PSPChargeResponse {
    
    private String status;
    private String pspRef;
    private String code;  
    
    public boolean isSuccess() {
        return "succeeded".equals(status);
    }
    
    public boolean isFailed() {
        return "failed".equals(status);
    }
}
