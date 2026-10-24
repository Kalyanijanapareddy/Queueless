
package com.queueless.dto;

public class BranchResponse {

    private Long id;
    private Long organizationId;
    private String name;
    private String branchCode;
    private String addressLine;
    private String city;
    private String state;
    private String postalCode;
    private String phone;
    private boolean active;

    public BranchResponse(Long id, Long organizationId, String name,
            String branchCode, String addressLine, String city,
            String state, String postalCode, String phone, boolean active) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.branchCode = branchCode;
        this.addressLine = addressLine;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.phone = phone;
        this.active = active;
    }

    public Long getId() { return id; }
    public Long getOrganizationId() { return organizationId; }
    public String getName() { return name; }
    public String getBranchCode() { return branchCode; }
    public String getAddressLine() { return addressLine; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getPhone() { return phone; }
    public boolean isActive() { return active; }
}

