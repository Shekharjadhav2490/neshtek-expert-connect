# Database

Oracle Autonomous Database schema for NeshTek Expert Connect.

The initial model will separate expert identity, skills, technical expertise, availability, consulting preferences, documents and verification so that the platform can later support customer requirements and expert matching.

## Initial tables

- EXPERT
- EXPERT_SKILL
- EXPERT_EXPERTISE
- EXPERT_AVAILABILITY
- EXPERT_CONSULTING
- EXPERT_CERTIFICATION
- EXPERT_DOCUMENT
- EXPERT_VERIFICATION

Database changes will be applied through Flyway from the Spring Boot application.