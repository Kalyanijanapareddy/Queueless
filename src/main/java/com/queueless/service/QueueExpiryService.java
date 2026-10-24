package com.queueless.service;

import com.queueless.entity.QueueEntry;
import com.queueless.repository.QueueEntryRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Marks abandoned WAITING queue entries as EXPIRED after 24 hours.
 * Historical entries are retained so users can still review their bookings.
 */
@Service
public class QueueExpiryService {

    private final QueueEntryRepository queueRepository;

    public QueueExpiryService(QueueEntryRepository queueRepository) {
        this.queueRepository = queueRepository;
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 60000)
    @Transactional
    public void expireOldWaitingEntries() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        List<QueueEntry> expiredEntries =
                queueRepository.findByStatusAndCreatedAtBefore("WAITING", cutoff);

        if (expiredEntries.isEmpty()) {
            return;
        }

        for (QueueEntry entry : expiredEntries) {
            entry.setStatus("EXPIRED");
        }
        queueRepository.saveAll(expiredEntries);
    }
}
