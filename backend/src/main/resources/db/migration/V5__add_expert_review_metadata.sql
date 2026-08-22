ALTER TABLE expert ADD (
    review_reason VARCHAR2(1000),
    reviewed_by VARCHAR2(150),
    reviewed_at TIMESTAMP
);
