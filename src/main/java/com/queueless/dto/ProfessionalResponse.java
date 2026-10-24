
package com.queueless.dto;

public class ProfessionalResponse {

    private Long id;
    private Long branchId;
    private String fullName;
    private String designation;
    private String qualification;
    private String specialization;
    private String email;
    private String phone;
    private Boolean active;

    public ProfessionalResponse(Long id, Long branchId, String fullName,
            String designation, String qualification, String specialization,
            String email, String phone, Boolean active) {
        this.id = id;
        this.branchId = branchId;
        this.fullName = fullName;
        this.designation = designation;
        this.qualification = qualification;
        this.specialization = specialization;
        this.email = email;
        this.phone = phone;
        this.active = active;
    }

    public Long getId() { return id; }
    public Long getBranchId() { return branchId; }
    public String getFullName() { return fullName; }
    public String getDesignation() { return designation; }
    public String getQualification() { return qualification; }
    public String getSpecialization() { return specialization; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Boolean getActive() { return active; }
}
