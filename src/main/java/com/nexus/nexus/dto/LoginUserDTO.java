package com.nexus.nexus.dto;

public class LoginUserDTO {

    private String username;
    private String password;

    // Constructor
    public LoginUserDTO(){}

    // Getter & Setter

    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
