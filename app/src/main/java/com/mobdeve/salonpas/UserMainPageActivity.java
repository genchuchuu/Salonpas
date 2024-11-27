package com.mobdeve.salonpas;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserMainPageActivity extends AppCompatActivity {

    private TextView greetingTextView, faqsTextView;
    private GridLayout servicesGrid;
    private EditText searchBar;
    private ImageView searchIcon;
    private DatabaseReference servicesRef;

    private List<Service> allServices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usermainpage);

        greetingTextView = findViewById(R.id.greeting);
        faqsTextView = findViewById(R.id.faqs);
        servicesGrid = findViewById(R.id.servicesGrid);

        loadGreeting();
        loadServices();
        setFAQsContent();

    }

    private void loadGreeting() {
        greetingTextView.setText("Hello, User!");
    }

    private void loadServices() {
        servicesRef = FirebaseDatabase.getInstance().getReference("Services");

        servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allServices.clear();
                for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                    Service service = serviceSnapshot.getValue(Service.class);
                    if (service != null) {
                        allServices.add(service);
                    }
                }
                displayServices(allServices);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserMainPageActivity.this, "Failed to load services: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayServices(List<Service> services) {
        servicesGrid.removeAllViews();

        for (Service service : services) {
            addServiceToGrid(service);
        }
    }

    private void addServiceToGrid(Service service) {
        View serviceItem = getLayoutInflater().inflate(R.layout.service_items, servicesGrid, false);
        TextView serviceName = serviceItem.findViewById(R.id.serviceName);
        TextView serviceDesc = serviceItem.findViewById(R.id.serviceDesc);
        TextView serviceDuration = serviceItem.findViewById(R.id.serviceDuration);
        TextView servicePrice = serviceItem.findViewById(R.id.servicePrice);
        ImageView serviceImage = serviceItem.findViewById(R.id.serviceImg);

        serviceName.setText(service.getName());
        serviceDesc.setText(service.getDescription());
        serviceDuration.setText(service.getDuration());
        servicePrice.setText(service.getPrice());

        if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
            Glide.with(this).load(service.getImageUrl()).into(serviceImage);
        } else {
            // Default image logic
            switch (service.getName().toLowerCase()) {
                case "hair treatment":
                    serviceImage.setImageResource(R.drawable.hairtreatment);
                    break;
                case "hair styling":
                    serviceImage.setImageResource(R.drawable.hairstyle);
                    break;
                case "haircut":
                    serviceImage.setImageResource(R.drawable.haircut);
                    break;
                case "hair color":
                    serviceImage.setImageResource(R.drawable.haircolor);
                    break;
                case "rebond":
                    serviceImage.setImageResource(R.drawable.rebond);
                    break;
                default:
                    serviceImage.setImageResource(R.drawable.placeholder);
                    break;
            }
        }

        serviceItem.setOnClickListener(v -> {
            Intent intent = new Intent(UserMainPageActivity.this, ViewService.class);
            intent.putExtra("name", service.getName());
            intent.putExtra("desc", service.getDescription());
            intent.putExtra("duration", service.getDuration());
            intent.putExtra("price", service.getPrice());
            intent.putExtra("imageUrl", service.getImageUrl());
            startActivity(intent);
        });

        servicesGrid.addView(serviceItem);
    }


    private void setFAQsContent() {
        String faqText = "1. What is the Salonpas Hair Salon app?" +
                "The Salonpas app is a salon appointment scheduling platform that allows clients to register, book appointments, view services and stylists, and manage their salon experience." +
                "2. What services are available through the app?" +
                "Clients can book haircuts, coloring, styling, and other hair treatments." +
                "3. How can I book an appointment?" +
                "Register, select a service, choose a stylist, and book a time slot." +
                "4. Can I choose my stylist?" +
                "Yes, you can view available stylists and choose based on their profiles." +
                "5. What happens if I can’t make it to my appointment?" +
                "You can reschedule or cancel your appointment through the app.";

        faqsTextView.setText(faqText);
    }

    public void openUserMainPage(View view) {
        startActivity(new Intent(UserMainPageActivity.this, UserMainPageActivity.class));
    }

    public void openServicesList(View view) {
        startActivity(new Intent(UserMainPageActivity.this, ServiceList.class));
    }

    public void openStylistList(View view) {
        startActivity(new Intent(UserMainPageActivity.this, ViewStylistList.class));
    }

    public void openReservationPage(View view) {
        startActivity(new Intent(UserMainPageActivity.this, AppointmentReservationActivity.class));
    }

    public void openNotificationPage(View view) {
        startActivity(new Intent(UserMainPageActivity.this, AppointmentNotificationActivity.class));
    }

    public void openProfilePage(View view) {
        startActivity(new Intent(UserMainPageActivity.this, ProfileActivity.class));
    }
}
