package com.zilpher.webdev2.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bio;
    private String phoneNumber;

    @OneToOne(mappedBy = "profile")
    private User user;

    public UserProfile() {}

    public UserProfile(String bio, String phoneNumber) {
        this.bio = bio;
        this.phoneNumber = phoneNumber;
    }

    public Long getId() { 
        return id; 
    }public String getBio() { 
        return bio; 
    }public String getPhoneNumber() { 
        return phoneNumber; 
    }public User getUser() { 
        return user; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }public void setBio(String bio) { 
        this.bio = bio; 
    }public void setPhoneNumber(String phoneNumber) { 
        this.phoneNumber = phoneNumber; 
    }public void setUser(User user) { 
        this.user = user; 
    }
}