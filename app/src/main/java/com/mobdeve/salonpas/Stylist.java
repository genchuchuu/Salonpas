package com.mobdeve.salonpas;

public class Stylist {
    private String firebaseKey;
    private String name;
    private String photo;
    private int yearsOfExperience;
    private double rating;
    private String services;

    public Stylist() {}

    public Stylist(String name, String photo, int years, double rating, String services) {
        this.name = name;
        this.photo = photo;
        this.yearsOfExperience = years;
        this.rating = rating;
        this.services = services;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public int getYearsOfExperience() { return yearsOfExperience; }
    public void setYearsOfExperience(int years) { this.yearsOfExperience = years; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getServices() { return services; }
    public void setServices(String services) { this.services = services; }

    public String getFirebaseKey() {
        return firebaseKey;
    }

    public void setFirebaseKey(String firebaseKey) {
        this.firebaseKey = firebaseKey;
    }

}
