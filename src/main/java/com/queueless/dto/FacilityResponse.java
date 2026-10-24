
package com.queueless.dto;

public class FacilityResponse {

    private Long id;
    private Long branchId;
    private String name;
    private String description;
    private boolean active;

    public FacilityResponse(Long id, Long branchId, String name,
                            String description, boolean active) {
        this.id = id;
        this.branchId = branchId;
        this.name = name;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public Long getBranchId() {
        return branchId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getActive() {
        return active;
    }
}

