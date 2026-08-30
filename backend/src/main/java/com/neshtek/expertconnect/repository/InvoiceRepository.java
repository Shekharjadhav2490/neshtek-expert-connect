package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.Invoice;
import com.neshtek.expertconnect.entity.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice,Long> {
 Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
 Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);
 Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
 boolean existsByEngagementId(Long engagementId);
}
