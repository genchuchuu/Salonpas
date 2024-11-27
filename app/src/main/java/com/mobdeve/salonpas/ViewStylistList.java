package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class ViewStylistList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StylistAdapter adapter;
    private List<Stylist> stylistList;
    private List<Stylist> allStylists;
    private EditText searchBar;
    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stylist);

        recyclerView = findViewById(R.id.recyclerView);
        searchBar = findViewById(R.id.searchBar);
        searchIcon = findViewById(R.id.searchIcon);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stylistList = new ArrayList<>();
        allStylists = new ArrayList<>();
        adapter = new StylistAdapter(stylistList, stylist -> {
            Intent intent = new Intent(this, StylistProfile.class);
            intent.putExtra("stylist_name", stylist.getName());
            intent.putExtra("stylist_photo", stylist.getPhoto());
            intent.putExtra("stylist_experience", stylist.getYearsOfExperience());
            intent.putExtra("stylist_rating", stylist.getRating());
            intent.putExtra("stylist_services", stylist.getServices());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        loadStylistList();
        setupSearch();
    }

    private void loadStylistList() {
        DatabaseReference stylistsRef = FirebaseDatabase.getInstance().getReference("Stylists");
        stylistsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stylistList.clear();
                allStylists.clear();
                for (DataSnapshot stylistSnapshot : snapshot.getChildren()) {
                    Stylist stylist = stylistSnapshot.getValue(Stylist.class);
                    if (stylist != null) {
                        stylistList.add(stylist);
                        allStylists.add(stylist);
                        Log.d("ViewStylistList", "Loaded Stylist: " + stylist.getName());
                    } else {
                        Log.e("ViewStylistList", "Null stylist object found in database.");
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewStylistList.this, "Failed to load stylists: " + error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("ViewStylistList", "Database error: " + error.getMessage());
            }
        });
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStylists(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchIcon.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            filterStylists(query);
        });
    }

    private void filterStylists(String query) {
        stylistList.clear();
        if (query.isEmpty()) {
            stylistList.addAll(allStylists);
        } else {
            for (Stylist stylist : allStylists) {
                if (stylist.getName().toLowerCase().contains(query.toLowerCase())) {
                    stylistList.add(stylist);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void openUserMainPage(View view) {
        Intent intent = new Intent(ViewStylistList.this, UserMainPageActivity.class);
        startActivity(intent);
    }

    public void openServicesList(View view) {
        Intent intent = new Intent(ViewStylistList.this, ServiceList.class);
        startActivity(intent);
    }

    public void openStylistList(View view) {
        Intent intent = new Intent(ViewStylistList.this, ViewStylistList.class);
        startActivity(intent);
    }

    public void openReservationPage(View view) {
        Intent intent = new Intent(ViewStylistList.this, AppointmentReservationActivity.class);
        startActivity(intent);
    }

    public void openNotificationPage(View view) {
        Intent intent = new Intent(ViewStylistList.this, AppointmentNotificationActivity.class);
        startActivity(intent);
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(ViewStylistList.this, ProfileActivity.class);
        startActivity(intent);
    }
}
