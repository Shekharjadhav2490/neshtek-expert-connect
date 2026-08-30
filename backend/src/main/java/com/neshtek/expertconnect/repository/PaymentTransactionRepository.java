package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction,Long> {
 boolean existsByPaymentReference(String paymentReference);
 Page<PaymentTransaction> findByInvoiceId(Long invoiceId, Pageable pageable);
 List<PaymentTransaction> findByInvoiceIdOrderByPaymentDateDesc(Long invoiceId);
}
