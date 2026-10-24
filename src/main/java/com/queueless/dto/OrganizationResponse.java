
package com.queueless.dto;

public class OrganizationResponse {

    private Long id;
    private String name;
    private String description;
    private String registrationNumber;
    private String email;
    private String phone;
    private String website;
    private boolean active;
    private Long categoryId;

    public OrganizationResponse() {
    }

    public OrganizationResponse(
            Long id,
            String name,
            String description,
            String registrationNumber,
            String email,
            String phone,
            String website,
            boolean active,
            Long categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.active = active;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public boolean isActive() {
        return active;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
