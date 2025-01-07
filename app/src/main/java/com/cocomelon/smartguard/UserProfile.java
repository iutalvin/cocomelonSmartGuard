package com.cocomelon.smartguard;

public class UserProfile {
    private String email;
    private String name;

    // Default constructor required for calls to DataSnapshot.getValue(UserProfile.class)
    public UserProfile() {
    }

    public UserProfile(String email, String name) {
        this.email = email;
        this.name = name;
    }

    // Getter methods
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    // Setter methods (optional, but recommended for updating profile data)
    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }
}
