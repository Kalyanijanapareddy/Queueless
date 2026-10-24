
package com.queueless.repository;

import com.queueless.entity.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProfessionalRepository
        extends JpaRepository<Professional, Long> {

    List<Professional> findByBranchIdAndActiveTrue(Long branchId);
}

