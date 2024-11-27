package com.mobdeve.salonpas;

public class Appointment {
    private String dateTime;
    private String serviceName;
    private String stylistName;
    private String userId;

    public Appointment(String dateTime, String serviceName, String stylistName, String userId) {
        this.dateTime = dateTime;
        this.serviceName = serviceName;
        this.stylistName = stylistName;
        this.userId = userId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getStylistName() {
        return stylistName;
    }

    public String getUserId() {
        return userId;
    }
}
