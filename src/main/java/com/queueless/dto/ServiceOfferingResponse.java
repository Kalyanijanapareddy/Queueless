package com.queueless.dto;

import java.math.BigDecimal;

public class ServiceOfferingResponse {

    private Long id;
    private Long branchId;
    private Long professionalId;
    private Long categoryId;
    private String categoryName;
    private String organizationName;
    private String branchName;
    private String professionalName;
    private String specialization;
    private String name;
    private String description;
    private Integer estimatedDurationMinutes;
    private BigDecimal baseFee;
    private Boolean active;

    public ServiceOfferingResponse(Long id, Long branchId,
            Long professionalId, Long categoryId, String categoryName,
            String organizationName, String branchName, String professionalName,
            String specialization, String name, String description,
            Integer estimatedDurationMinutes, BigDecimal baseFee,
            Boolean active) {
        this.id = id;
        this.branchId = branchId;
        this.professionalId = professionalId;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.organizationName = organizationName;
        this.branchName = branchName;
        this.professionalName = professionalName;
        this.specialization = specialization;
        this.name = name;
        this.description = description;
        this.estimatedDurationMinutes = estimatedDurationMinutes;
        this.baseFee = baseFee;
        this.active = active;
    }

    public Long getId() { return id; }
    public Long getBranchId() { return branchId; }
    public Long getProfessionalId() { return professionalId; }
    public Long getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
    public String getOrganizationName() { return organizationName; }
    public String getBranchName() { return branchName; }
    public String getProfessionalName() { return professionalName; }
    public String getSpecialization() { return specialization; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getEstimatedDurationMinutes() { return estimatedDurationMinutes; }
    public BigDecimal getBaseFee() { return baseFee; }
    public Boolean getActive() { return active; }
}
