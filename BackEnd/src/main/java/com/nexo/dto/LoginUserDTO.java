package com.nexo.dto;

public class LoginUserDTO {

    private String email;
    private String password;

    // Constructor
    public LoginUserDTO(){}

    // Getter & Setter

    public String getPassword() {
        return password;
    }
    public String getEmail() {
        return email;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
