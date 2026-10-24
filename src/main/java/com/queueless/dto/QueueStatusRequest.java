
package com.queueless.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class QueueStatusRequest {

    @NotBlank(message = "Status is required")
    @Pattern(
        regexp = "WAITING|SERVING|COMPLETED|CANCELLED",
        message = "Invalid queue status"
    )
    private String status;

    public QueueStatusRequest() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
