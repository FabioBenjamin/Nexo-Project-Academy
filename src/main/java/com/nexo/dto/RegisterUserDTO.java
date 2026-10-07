package com.nexo.nexo.dto;

public class RegisterUserDTO {

    private String email;
    private String username;
    private String password;

    // Constructor
    public RegisterUserDTO(){}

    // Getter & Setter
    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public String getUsername() {
        return username;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
