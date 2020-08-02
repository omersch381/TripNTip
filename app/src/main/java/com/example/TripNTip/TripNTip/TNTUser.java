package com.example.TripNTip.TripNTip;

public class TNTUser {

    private String username;
    private String email;
    private String password;
    private String country;

    public TNTUser() {
    }

    public TNTUser(String email, String userName, String password, String country) {
        this.email = email;
        this.username = userName;
        this.password = password;
        this.country = country;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getCountry() {
        return this.country;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}