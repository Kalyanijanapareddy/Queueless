
package com.queueless.dto;

import java.time.LocalDateTime;

public class QueueEntryResponse {

    private Long id;
    private Long userId;
    private Long serviceId;
    private Integer tokenNumber;
    private String status;
    private LocalDateTime createdAt;
    private Long peopleAhead;

    public QueueEntryResponse(Long id, Long userId, Long serviceId,
            Integer tokenNumber, String status,
            LocalDateTime createdAt, Long peopleAhead) {
        this.id = id;
        this.userId = userId;
        this.serviceId = serviceId;
        this.tokenNumber = tokenNumber;
        this.status = status;
        this.createdAt = createdAt;
        this.peopleAhead = peopleAhead;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getServiceId() { return serviceId; }
    public Integer getTokenNumber() { return tokenNumber; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getPeopleAhead() { return peopleAhead; }
}
