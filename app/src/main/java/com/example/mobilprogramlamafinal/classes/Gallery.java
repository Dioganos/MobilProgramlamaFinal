package com.example.mobilprogramlamafinal.classes;

public class Gallery {
    String image;
    String username;
    String label;
    String description;

    public Gallery(String image, String username, String label, String description) {
        this.image = image;
        this.username = username;
        this.label = label;
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Gallery() {
    }
}