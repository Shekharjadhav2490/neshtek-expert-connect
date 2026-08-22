ALTER TABLE expert_expertise DROP CONSTRAINT ck_expertise_word_count;

ALTER TABLE expert_expertise
    ADD CONSTRAINT ck_expertise_word_count CHECK (word_count BETWEEN 1 AND 1000);
