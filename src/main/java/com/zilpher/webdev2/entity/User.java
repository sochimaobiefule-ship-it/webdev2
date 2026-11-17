package com.zilpher.webdev2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", referencedColumnName = "id")
    private UserProfile profile;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, length = 32)
    private String role = "READER";

    public User() {}

    public User(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }public String getUsername() {
        return username;
    }public UserProfile getProfile() {
        return profile;
    }public String getEmail() {
        return email;
    }public String getPasswordHash() {
        return passwordHash;
    }public String getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }public void setUsername(String username) {
        this.username = username;
    }public void setProfile(UserProfile profile) {
        this.profile = profile;
        if (profile != null) {
            profile.setUser(this);
        }
    }public void setEmail(String email) {
        this.email = email;
    }public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }public void setRole(String role) {
        this.role = (role == null || role.isBlank()) ? "READER" : role;
    }
}