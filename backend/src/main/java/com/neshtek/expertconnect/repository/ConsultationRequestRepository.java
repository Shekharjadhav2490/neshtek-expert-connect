package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.ConsultationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequest, Long> {

    @Override
    @EntityGraph(attributePaths = {"customer", "requirement", "expert"})
    Page<ConsultationRequest> findAll(Pageable pageable);

    Page<ConsultationRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);
    Page<ConsultationRequest> findByExpertIdOrderByCreatedAtDesc(Long expertId, Pageable pageable);
    boolean existsByRequirementId(Long requirementId);
}
