package com.neshtek.expertconnect.repository;

import com.neshtek.expertconnect.entity.CustomerRequirement;
import com.neshtek.expertconnect.entity.CustomerRequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface CustomerRequirementRepository extends JpaRepository<CustomerRequirement, Long>, JpaSpecificationExecutor<CustomerRequirement> {
    List<CustomerRequirement> findByStatus(CustomerRequirementStatus status);
}
