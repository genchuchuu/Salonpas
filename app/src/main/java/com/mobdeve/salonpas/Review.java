package com.mobdeve.salonpas;

public class Review {
    private String appointmentId;
    private String serviceName;
    private String stylistName;
    private float serviceRating;
    private float stylistRating;
    private String comments;

    public Review() {}

    public Review(String appointmentId, String serviceName, String stylistName, float serviceRating, float stylistRating, String comments) {
        this.appointmentId = appointmentId;
        this.serviceName = serviceName;
        this.stylistName = stylistName;
        this.serviceRating = serviceRating;
        this.stylistRating = stylistRating;
        this.comments = comments;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getStylistName() {
        return stylistName;
    }

    public float getServiceRating() {
        return serviceRating;
    }

    public float getStylistRating() {
        return stylistRating;
    }

    public String getComments() {
        return comments;
    }
}
