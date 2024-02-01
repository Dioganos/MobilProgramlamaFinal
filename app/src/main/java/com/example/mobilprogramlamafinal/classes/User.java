package com.example.mobilprogramlamafinal.classes;

public class User {
    String name;
    String lastName;
    String emial;

    public User(String emial, String lastName, String name) {
        this.name = name;
        this.lastName = lastName;
        this.emial = emial;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmial() {
        return emial;
    }

    public void setEmial(String emial) {
        this.emial = emial;
    }
}
