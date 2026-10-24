
package com.queueless.repository;

import com.queueless.entity.ServiceCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceCategoryRepository
        extends JpaRepository<ServiceCategory, Long> {
}

