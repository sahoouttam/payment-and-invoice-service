package com.payment.and.invoice.service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.and.invoice.service.model.Business;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {
    
}
