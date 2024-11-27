package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ServiceList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ServiceAdapter adapter;
    private List<Service> serviceList;
    private List<Service> allServices;
    private EditText searchBar;
    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_list);

        // Initialize views
        recyclerView = findViewById(R.id.ServiceListRecView);
        searchBar = findViewById(R.id.searchBar);
        searchIcon = findViewById(R.id.searchIcon);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceList = new ArrayList<>();
        allServices = new ArrayList<>();
        adapter = new ServiceAdapter(serviceList, service -> {
            Intent intent = new Intent(this, ViewService.class);
            intent.putExtra("name", service.getName());
            intent.putExtra("desc", service.getDescription());
            intent.putExtra("duration", service.getDuration());
            intent.putExtra("price", service.getPrice());
            intent.putExtra("imageUrl", service.getImageUrl());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadServiceList();
        setupSearch();
    }

    private void loadServiceList() {
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("Services");
        servicesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                serviceList.clear();
                allServices.clear();
                for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {
                    Service service = serviceSnapshot.getValue(Service.class);
                    if (service != null) {
                        serviceList.add(service);
                        allServices.add(service);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ServiceList.this, "Failed to load services: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                filterServices(query.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        searchIcon.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            filterServices(query);
        });
    }

    private void filterServices(String query) {
        List<Service> filteredServices = new ArrayList<>();

        for (Service service : allServices) {
            if (service.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredServices.add(service);
            }
        }

        serviceList.clear();
        serviceList.addAll(filteredServices);
        adapter.notifyDataSetChanged();
    }

    public void openUserMainPage(View view) {
        Intent intent = new Intent(ServiceList.this, UserMainPageActivity.class);
        startActivity(intent);
    }

    public void openServicesList(View view) {
        Intent intent = new Intent(ServiceList.this, ServiceList.class);
        startActivity(intent);
    }

    public void openStylistList(View view) {
        Intent intent = new Intent(ServiceList.this, ViewStylistList.class);
        startActivity(intent);
    }

    public void openReservationPage(View view) {
        Intent intent = new Intent(ServiceList.this, AppointmentReservationActivity.class);
        startActivity(intent);
    }

    public void openNotificationPage(View view) {
        Intent intent = new Intent(ServiceList.this, AppointmentNotificationActivity.class);
        startActivity(intent);
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(ServiceList.this, ProfileActivity.class);
        startActivity(intent);
    }
}
