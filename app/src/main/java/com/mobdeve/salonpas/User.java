package com.mobdeve.salonpas;

public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String birthdate;
    private String gender;
    private String contact;
    private String profilePictureUrl; // Optional field

    public User() {}

    public User(String firstName, String lastName, String email, String birthdate, String gender, String contact) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.contact = contact;
        this.profilePictureUrl = null;
    }

    public User(String firstName, String lastName, String email, String birthdate, String gender, String contact, String profilePictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.contact = contact;
        this.profilePictureUrl = profilePictureUrl;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBirthdate() { return birthdate; }
    public void setBirthdate(String birthdate) { this.birthdate = birthdate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
