package com.mobdeve.salonpas;

public class Service {
    private String id;
    private String Name;
    private String Description;
    private String Duration;
    private String Price;
    private String ImageUrl; // Changed to String for image URL

    public Service() {
        // Default constructor for Firebase
    }

    public Service(String Name, String Description, String Duration, String Price, String ImageUrl) {
        this.id = "";
        this.Name = Name;
        this.Description = Description;
        this.Duration = Duration;
        this.Price = Price;
        this.ImageUrl = ImageUrl;
    }

    public String getId() { return id; }
    public String getName() { return Name; }
    public String getDescription() { return Description; }
    public String getDuration() { return Duration; }
    public String getPrice() { return Price; }
    public String getImageUrl() { return ImageUrl; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { Name = name; }
    public void setDescription(String description) { Description = description; }
    public void setDuration(String duration) { Duration = duration; }
    public void setPrice(String price) { Price = price; }
    public void setImageUrl(String imageUrl) { ImageUrl = imageUrl; }
}
