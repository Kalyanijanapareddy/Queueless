
package com.queueless.repository;

import com.queueless.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ServiceOfferingRepository
        extends JpaRepository<ServiceOffering, Long> {

    List<ServiceOffering> findByBranchIdAndActiveTrue(Long branchId);

    List<ServiceOffering> findByProfessionalIdAndActiveTrue(Long professionalId);
}

