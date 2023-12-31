package com.example.googlemapsandplaces;

import java.io.Serializable;

public class UserDetails implements Serializable {

    private static final String TAG = ".UserDetails";

    private String email, name, surname;

    public UserDetails(){}

    public UserDetails(String email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
