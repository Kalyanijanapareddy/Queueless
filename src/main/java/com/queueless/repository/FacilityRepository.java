
package com.queueless.repository;

import com.queueless.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FacilityRepository extends JpaRepository<Facility, Long> {

    List<Facility> findByBranchIdAndActiveTrue(Long branchId);
}

