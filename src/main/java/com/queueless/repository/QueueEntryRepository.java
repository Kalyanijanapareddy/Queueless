
package com.queueless.repository;

import com.queueless.entity.QueueEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.time.LocalDateTime;

public interface QueueEntryRepository
        extends JpaRepository<QueueEntry, Long> {

    List<QueueEntry> findByServiceIdOrderByCreatedAtAsc(Long serviceId);

    List<QueueEntry> findByUserIdOrderByCreatedAtDesc(Long userId);

    long countByServiceIdAndStatus(Long serviceId, String status);
    
    List<QueueEntry> findByServiceIdAndStatusOrderByCreatedAtAsc(
            Long serviceId, String status, Pageable pageable);

    List<QueueEntry> findByStatusAndCreatedAtBefore(String status, LocalDateTime cutoff);

    boolean existsByServiceIdAndTokenNumber(
            Long serviceId, Integer tokenNumber);

    @Query("""
        SELECT COUNT(q)
        FROM QueueEntry q
        WHERE q.service.id = :serviceId
          AND q.status = 'WAITING'
          AND q.createdAt < :createdAt
    """)
    long countWaitingAhead(
            @Param("serviceId") Long serviceId,
            @Param("createdAt") java.time.LocalDateTime createdAt);
}


