package com.example.TripNTip.TripNTip;

public class TNTUser {

    private String username;
    private String email;
    private String password;

    public TNTUser(String email, String userName, String password) {
        this.email = email;
        this.username = userName;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }
}