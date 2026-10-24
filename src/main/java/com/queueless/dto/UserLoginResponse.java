
package com.queueless.dto;

public class UserLoginResponse {

    private Long id;
    private String fullName;
    private String email;
    private String roleName;
    private String message;
    private String token;

    public UserLoginResponse(
            Long id,
            String fullName,
            String email,
            String roleName,
            String message,
            String token) {

        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.message = message;
        this.token = token;
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

    public String getRoleName() {
        return roleName;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}
