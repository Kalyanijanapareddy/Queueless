

package com.queueless.controller;

import com.queueless.dto.QueueEntryRequest;

import com.queueless.dto.QueueEntryResponse;
import com.queueless.entity.QueueEntry;
import com.queueless.entity.ServiceOffering;
import com.queueless.entity.User;
import com.queueless.repository.QueueEntryRepository;
import com.queueless.repository.ServiceOfferingRepository;
import com.queueless.repository.UserRepository;

import org.springframework.data.domain.PageRequest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/queue")
public class QueueEntryController {

    private final QueueEntryRepository queueRepository;
    private final UserRepository userRepository;
    private final ServiceOfferingRepository serviceRepository;

    public QueueEntryController(
            QueueEntryRepository queueRepository,
            UserRepository userRepository,
            ServiceOfferingRepository serviceRepository) {
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
    }


private QueueEntryResponse toResponse(QueueEntry entry) {

    long peopleAhead = 0;

    if ("WAITING".equals(entry.getStatus())) {
        peopleAhead = queueRepository.countWaitingAhead(
                entry.getService().getId(),
                entry.getCreatedAt());
    }

    return new QueueEntryResponse(
            entry.getId(),
            entry.getUser().getId(),
            entry.getService().getId(),
            entry.getTokenNumber(),
            entry.getStatus(),
            entry.getCreatedAt(),
            peopleAhead);
}


    @PostMapping
    public ResponseEntity<QueueEntryResponse> joinQueue(
            @Valid @RequestBody QueueEntryRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        ServiceOffering service = serviceRepository
                .findById(request.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Service not found"));

        int nextToken = queueRepository
                .findByServiceIdOrderByCreatedAtAsc(service.getId())
                .stream()
                .mapToInt(QueueEntry::getTokenNumber)
                .max()
                .orElse(0) + 1;

        QueueEntry entry = new QueueEntry();
        entry.setUser(user);
        entry.setService(service);
        entry.setTokenNumber(nextToken);
        entry.setStatus("WAITING");

        QueueEntry saved = queueRepository.save(entry);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(saved));
    }

    @GetMapping
    public List<QueueEntryResponse> getAllQueueEntries() {
        return queueRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/user/{userId}")
    public List<QueueEntryResponse> getQueueByUser(
            @PathVariable Long userId) {
        return queueRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/service/{serviceId}")
    public List<QueueEntryResponse> getQueueByService(
            @PathVariable Long serviceId) {
        return queueRepository
                .findByServiceIdOrderByCreatedAtAsc(serviceId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

@PatchMapping("/{id}/status")
public ResponseEntity<QueueEntryResponse> updateQueueStatus(
        @PathVariable Long id,
        @Valid @RequestBody com.queueless.dto.QueueStatusRequest request) {

    QueueEntry entry = queueRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Queue entry not found"));

    String requestedStatus = request.getStatus();

    if ("WAITING".equals(entry.getStatus())
            && !"CANCELLED".equals(requestedStatus)) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A waiting token can only be cancelled by this endpoint. Use Call Next to start service.");
    }

    if ("SERVING".equals(entry.getStatus())
            && !"COMPLETED".equals(requestedStatus)) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A serving token can only be marked COMPLETED.");
    }

    if ("CANCELLED".equals(entry.getStatus())
            || "COMPLETED".equals(entry.getStatus())
            || "EXPIRED".equals(entry.getStatus())) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "This queue entry is already in a final state.");
    }

    entry.setStatus(requestedStatus);

    QueueEntry savedEntry = queueRepository.save(entry);

    return ResponseEntity.ok(toResponse(savedEntry));
}


@PostMapping("/service/{serviceId}/call-next")
public ResponseEntity<QueueEntryResponse> callNextCustomer(
        @PathVariable Long serviceId) {

    long currentlyServing =
            queueRepository.countByServiceIdAndStatus(
                    serviceId, "SERVING");

    if (currentlyServing > 0) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "A customer is already being served");
    }

    List<QueueEntry> waitingEntries =
            queueRepository.findByServiceIdAndStatusOrderByCreatedAtAsc(
                    serviceId, "WAITING", PageRequest.of(0, 1));

    if (waitingEntries.isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No waiting customers");
    }

    QueueEntry nextEntry = waitingEntries.get(0);
    nextEntry.setStatus("SERVING");

    QueueEntry savedEntry =
            queueRepository.save(nextEntry);

    return ResponseEntity.ok(toResponse(savedEntry));
}

@GetMapping("/service/{serviceId}/waiting")
public List<QueueEntryResponse> getWaitingQueue(
        @PathVariable Long serviceId) {

    return queueRepository
            .findByServiceIdAndStatusOrderByCreatedAtAsc(
                    serviceId, "WAITING", org.springframework.data.domain.PageRequest.of(0, 100))
            .stream()
            .map(this::toResponse)
            .toList();
}


}
