package com.payment.and.invoice.service.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthenticationUtils {
    
    public static AuthenticationPrincipal getCurrentBusiness() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                        .getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() 
                                        instanceof AuthenticationPrincipal)) {
            throw new IllegalStateException("Not authenticated");
        }
        return (AuthenticationPrincipal) authentication.getPrincipal();
    }

    public static Long getBusinessId() {
        return getCurrentBusiness().getBusinessId();
    }

}
