
package com.queueless.repository;

import com.queueless.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByOrganizationIdAndActiveTrue(Long organizationId);
}

