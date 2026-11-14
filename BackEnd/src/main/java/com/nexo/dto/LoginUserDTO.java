package com.nexo.dto;

public class LoginUserDTO {

    private String user;
    private String password;

    // Constructor
    public LoginUserDTO(){}

    // Getter & Setter

    public String getPassword() {
        return password;
    }
    public String getUser() {
        return user;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUser(String email) {
        this.user = user;
    }
}
