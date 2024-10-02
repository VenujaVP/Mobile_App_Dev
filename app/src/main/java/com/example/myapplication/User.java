package com.example.myapplication;

public class User {
    private String username;
    private String email;
    private String profileImageUrl;  // Field for profile image URL

    // Default constructor required for calls to DataSnapshot.getValue(User.class)
    public User() {}

    // Constructor with all fields
    public User(String username, String email, String profileImageUrl) {
        this.username = username;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    // Constructor with only username and email
    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.profileImageUrl = ""; // Set a default value or leave it empty
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
