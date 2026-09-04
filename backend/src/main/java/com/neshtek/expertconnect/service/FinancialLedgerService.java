package com.neshtek.expertconnect.service;

import com.neshtek.expertconnect.dto.FinancialLedgerAdjustmentRequest;
import com.neshtek.expertconnect.dto.FinancialLedgerEntryResponse;
import com.neshtek.expertconnect.dto.FinancialReconciliationResponse;
import com.neshtek.expertconnect.entity.*;
import com.neshtek.expertconnect.exception.ResourceNotFoundException;
import com.neshtek.expertconnect.repository.*;
import com.neshtek.expertconnect.security.ResourceAuthorizationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
public class FinancialLedgerService {
    private final FinancialLedgerEntryRepository ledger;
    private final CustomerRepository customers;
    private final ExpertRepository experts;
    private final EngagementRepository engagements;
    private final InvoiceRepository invoices;
    private final PaymentTransactionRepository payments;
    private final SettlementRepository settlements;
    private final ExpertReplacementRequestRepository replacements;
    private final ResourceAuthorizationService authorization;

    public FinancialLedgerService(FinancialLedgerEntryRepository ledger, CustomerRepository customers,
                                  ExpertRepository experts, EngagementRepository engagements,
                                  InvoiceRepository invoices, PaymentTransactionRepository payments,
                                  SettlementRepository settlements, ExpertReplacementRequestRepository replacements,
                                  ResourceAuthorizationService authorization) {
        this.ledger = ledger; this.customers = customers; this.experts = experts; this.engagements = engagements;
        this.invoices = invoices; this.payments = payments; this.settlements = settlements; this.replacements = replacements;
        this.authorization = authorization;
    }

    @Transactional(readOnly = true)
    public Page<FinancialLedgerEntryResponse> byCustomer(Long customerId, Pageable pageable) {
        authorization.assertCustomerOwns(customerId);
        return ledger.findByCustomerIdOrderByOccurredAtDesc(customerId, pageable).map(FinancialLedgerEntryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<FinancialLedgerEntryResponse> byExpert(Long expertId, Pageable pageable) {
        authorization.assertExpertOwns(expertId);
        return ledger.findByExpertIdOrderByOccurredAtDesc(expertId, pageable).map(FinancialLedgerEntryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<FinancialLedgerEntryResponse> byEngagement(Long engagementId, Pageable pageable) {
        Engagement e = engagements.findWithDetailsById(engagementId)
                .orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + engagementId));
        authorization.assertCanAccess(e);
        return ledger.findByEngagementIdOrderByOccurredAtDesc(engagementId, pageable).map(FinancialLedgerEntryResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<FinancialLedgerEntryResponse> all(Pageable pageable) {
        requireAdmin();
        return ledger.findAllByOrderByOccurredAtDesc(pageable).map(FinancialLedgerEntryResponse::from);
    }

    @Transactional(readOnly = true)
    public FinancialReconciliationResponse reconcileCustomer(Long customerId) {
        authorization.assertCustomerOwns(customerId);
        BigDecimal debits = ledger.sumCustomerAmount(customerId, FinancialLedgerDirection.DEBIT);
        BigDecimal credits = ledger.sumCustomerAmount(customerId, FinancialLedgerDirection.CREDIT);
        return new FinancialReconciliationResponse(customerId, null, debits, credits,
                debits.subtract(credits), null, null, null, true);
    }

    @Transactional(readOnly = true)
    public FinancialReconciliationResponse reconcileExpert(Long expertId) {
        authorization.assertExpertOwns(expertId);
        BigDecimal earnings = ledger.sumExpertAmount(expertId, FinancialLedgerEntryType.EXPERT_EARNING);
        BigDecimal payouts = ledger.sumExpertAmount(expertId, FinancialLedgerEntryType.EXPERT_PAYOUT);
        return new FinancialReconciliationResponse(null, expertId, null, null, null, earnings, payouts,
                earnings.subtract(payouts), earnings.subtract(payouts).signum() >= 0);
    }

    @Transactional
    public FinancialLedgerEntryResponse createAdjustment(FinancialLedgerAdjustmentRequest request) {
        requireAdmin();
        Customer customer = customers.findById(request.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + request.customerId()));
        FinancialLedgerEntry entry = new FinancialLedgerEntry();
        entry.setCustomer(customer);
        if (request.expertId() != null) entry.setExpert(experts.findById(request.expertId()).orElseThrow(() -> new ResourceNotFoundException("Expert not found: " + request.expertId())));
        if (request.engagementId() != null) entry.setEngagement(engagements.findById(request.engagementId()).orElseThrow(() -> new ResourceNotFoundException("Engagement not found: " + request.engagementId())));
        if (request.replacementRequestId() != null) entry.setReplacementRequest(replacements.findById(request.replacementRequestId()).orElseThrow(() -> new ResourceNotFoundException("Replacement request not found: " + request.replacementRequestId())));
        if (request.parentEntryId() != null) entry.setParentEntry(ledger.findById(request.parentEntryId()).orElseThrow(() -> new ResourceNotFoundException("Parent ledger entry not found: " + request.parentEntryId())));
        entry.setEntryType(request.entryType()); entry.setDirection(request.direction()); entry.setAmount(request.amount());
        entry.setCurrencyCode(request.currencyCode().trim().toUpperCase()); entry.setSourceType(request.sourceType().trim()); entry.setSourceId(request.sourceId().trim());
        entry.setIdempotencyKey(request.sourceType().trim() + ":" + request.sourceId().trim() + ":" + request.entryType().name());
        entry.setDescription(request.description().trim()); entry.setCreatedBy("ADMIN");
        return FinancialLedgerEntryResponse.from(saveIdempotent(entry));
    }

    @Transactional
    public void postInvoiceIssued(Invoice invoice) {
        saveIdempotent(base(invoice.getCustomer(), invoice.getEngagement(), invoice.getCurrencyCode(), invoice.getTotalAmount(),
                FinancialLedgerEntryType.INVOICE_CHARGE, FinancialLedgerDirection.DEBIT, "INVOICE", invoice.getId().toString(),
                "Invoice " + invoice.getInvoiceNumber() + " issued", invoice, null, null, null));
    }

    @Transactional
    public void postCustomerPayment(PaymentTransaction payment) {
        Invoice invoice = payment.getInvoice();
        saveIdempotent(base(invoice.getCustomer(), invoice.getEngagement(), payment.getCurrencyCode(), payment.getAmount(),
                FinancialLedgerEntryType.CUSTOMER_PAYMENT, FinancialLedgerDirection.CREDIT, "PAYMENT", payment.getId().toString(),
                "Customer payment " + payment.getPaymentReference(), invoice, payment, null, null));
    }

    @Transactional
    public void postPaymentRefund(PaymentTransaction payment) {
        Invoice invoice = payment.getInvoice();
        saveIdempotent(base(invoice.getCustomer(), invoice.getEngagement(), payment.getCurrencyCode(), payment.getAmount(),
                FinancialLedgerEntryType.PAYMENT_REFUND, FinancialLedgerDirection.DEBIT, "PAYMENT_REFUND", payment.getId().toString(),
                "Refund for payment " + payment.getPaymentReference(), invoice, payment, null, null));
    }

    @Transactional
    public void postSettlementEarning(Settlement settlement) {
        saveIdempotent(base(settlement.getEngagement().getCustomer(), settlement.getEngagement(), settlement.getCurrencyCode(), settlement.getGrossAmount(),
                FinancialLedgerEntryType.EXPERT_EARNING, FinancialLedgerDirection.CREDIT, "SETTLEMENT_EARNING", settlement.getId().toString(),
                "Expert earning for settlement #" + settlement.getId(), null, null, settlement, null));
    }

    @Transactional
    public void postSettlementPayout(Settlement settlement) {
        saveIdempotent(base(settlement.getEngagement().getCustomer(), settlement.getEngagement(), settlement.getCurrencyCode(), settlement.getGrossAmount(),
                FinancialLedgerEntryType.EXPERT_PAYOUT, FinancialLedgerDirection.DEBIT, "SETTLEMENT_PAYOUT", settlement.getId().toString(),
                "Expert payout for settlement #" + settlement.getId(), null, null, settlement, null));
    }

    private FinancialLedgerEntry base(Customer customer, Engagement engagement, String currency, BigDecimal amount,
                                      FinancialLedgerEntryType type, FinancialLedgerDirection direction, String sourceType,
                                      String sourceId, String description, Invoice invoice, PaymentTransaction payment,
                                      Settlement settlement, ExpertReplacementRequest replacement) {
        FinancialLedgerEntry e = new FinancialLedgerEntry(); e.setCustomer(customer); e.setEngagement(engagement); e.setCurrencyCode(currency);
        e.setAmount(amount); e.setEntryType(type); e.setDirection(direction); e.setSourceType(sourceType); e.setSourceId(sourceId);
        e.setIdempotencyKey(sourceType + ":" + sourceId + ":" + type.name()); e.setDescription(description); e.setCreatedBy("SYSTEM");
        if (engagement != null) e.setExpert(engagement.getExpert()); e.setInvoice(invoice); e.setPayment(payment); e.setSettlement(settlement); e.setReplacementRequest(replacement);
        return e;
    }

    private FinancialLedgerEntry saveIdempotent(FinancialLedgerEntry entry) {
        return ledger.findByIdempotencyKey(entry.getIdempotencyKey()).orElseGet(() -> ledger.save(entry));
    }

    private void requireAdmin() { if (!authorization.isAdmin()) throw new AccessDeniedException("Admin access required"); }
}
