package com.nexo.nexo.models.classes;

import jakarta.persistence.*;

import java.time.LocalDate;
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

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpires_at(LocalDateTime expires_at) {
        this.expires_at = expires_at;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
