package com.example.eticket.Model;

import java.util.List;
import java.util.Map;

public class User {
    private String Name;
    private String Email;
    private Map<String, Object> userSeats;
    public User(){
    }

    public User(String name, String email, Map<String, Object> userSeats) {
        Name = name;
        Email = email;
        this.userSeats = userSeats;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Map<String, Object> getUserSeats() {
        return userSeats;
    }

    public void setUserSeats(Map<String, Object> userSeats) {
        this.userSeats = userSeats;
    }
}
