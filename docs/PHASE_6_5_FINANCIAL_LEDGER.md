# Phase 6.5 — Financial Ledger

## Scope
- Immutable financial ledger entries.
- Idempotent posting for invoice issue, customer payment, payment refund, settlement earning and expert payout.
- Customer, expert, engagement and admin ledger views.
- Customer/expert reconciliation endpoints.
- Admin-only financial adjustments for credits, refunds, platform fees and replacement adjustments.
- Replacement financial resolution remains linked through `replacementRequestId` and parent ledger entry support.

## Accounting rules
1. Ledger rows are append-only from the application perspective.
2. Do not update or delete a historical ledger row to correct a financial event.
3. Corrections must be posted as a new compensating/adjustment entry.
4. Automatic postings use an idempotency key so retries do not duplicate financial events.
5. Invoice charge is posted when an invoice is issued.
6. Customer payment is posted after a successful payment transaction is recorded.
7. Payment refund is posted when a successful payment is refunded.
8. Expert earning is posted when a settlement reaches `APPROVED_FOR_PAYOUT`.
9. Expert payout is posted when a settlement reaches `PAID`.

## APIs
- `GET /api/v1/financial-ledger/customer/{customerId}`
- `GET /api/v1/financial-ledger/customer/{customerId}/reconciliation`
- `GET /api/v1/financial-ledger/expert/{expertId}`
- `GET /api/v1/financial-ledger/expert/{expertId}/reconciliation`
- `GET /api/v1/financial-ledger/engagement/{engagementId}`
- `GET /api/v1/financial-ledger` (ADMIN)
- `POST /api/v1/financial-ledger/adjustments` (ADMIN)

## Manual adjustment example
```json
{
  "customerId": 1001,
  "expertId": 2001,
  "engagementId": 3001,
  "replacementRequestId": 4001,
  "parentEntryId": 5001,
  "entryType": "REPLACEMENT_ADJUSTMENT",
  "direction": "DEBIT",
  "amount": 125.00,
  "currencyCode": "INR",
  "sourceType": "REPLACEMENT",
  "sourceId": "4001",
  "description": "Replacement financial adjustment"
}
```

## Database
`V25__financial_ledger.sql` creates `FINANCIAL_LEDGER_ENTRY` and indexes. The replacement financial resolution migration is normalized to `V21__replacement_financial_resolution.sql` so there is only one V20 migration.
