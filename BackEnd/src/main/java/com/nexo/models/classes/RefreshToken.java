package com.nexo.models.classes;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Long id;

    @Column(nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expires_at;

    @ManyToOne // N:1
    @JoinColumn(name = "user_id") // Find FK
    private User user;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean revoked;

    public Long getId() {
        return id;
    }

    public LocalDateTime getExpires_at() {
        return expires_at;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Boolean isRevoked(){ return revoked; }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpires_at(LocalDateTime expires_at) {
        this.expires_at = expires_at;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRevoked(Boolean revoked){
        this.revoked = revoked;
    }
}
