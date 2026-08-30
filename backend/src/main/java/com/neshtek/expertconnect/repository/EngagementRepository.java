package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.Engagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EngagementRepository extends JpaRepository<Engagement, Long> {
    @EntityGraph(attributePaths = {"consultationRequest", "customer", "expert", "requirement"})
    Optional<Engagement> findWithDetailsById(Long id);

    @EntityGraph(attributePaths = {"consultationRequest", "customer", "expert", "requirement"})
    Optional<Engagement> findByConsultationRequestId(Long consultationRequestId);

    @EntityGraph(attributePaths = {"consultationRequest", "customer", "expert", "requirement"})
    Page<Engagement> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    @EntityGraph(attributePaths = {"consultationRequest", "customer", "expert", "requirement"})
    Page<Engagement> findByExpertIdOrderByCreatedAtDesc(Long expertId, Pageable pageable);

    boolean existsByConsultationRequestId(Long consultationRequestId);
    boolean existsByRequirementId(Long requirementId);

    @EntityGraph(attributePaths = {"consultationRequest", "customer", "expert", "requirement"})
    Page<Engagement> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
