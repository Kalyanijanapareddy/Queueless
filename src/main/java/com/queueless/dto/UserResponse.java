
package com.queueless.dto;

public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private boolean emailVerified;
    private boolean active;

    public UserResponse(Long id, String fullName, String email,
                        String phone, boolean emailVerified,
                        boolean active) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.emailVerified = emailVerified;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean getEmailVerified() {
        return emailVerified;
    }

    public boolean getActive() {
        return active;
    }
}

