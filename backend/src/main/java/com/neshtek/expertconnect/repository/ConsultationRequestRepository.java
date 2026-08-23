package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.ConsultationRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRequestRepository extends JpaRepository<ConsultationRequest, Long> {
    Page<ConsultationRequest> findByCustomerId(Long customerId, Pageable pageable);
    Page<ConsultationRequest> findByExpertId(Long expertId, Pageable pageable);
}
