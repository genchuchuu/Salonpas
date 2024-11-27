package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

public class ManageStylist extends AppCompatActivity {

    private RecyclerView recyclerView;
    private StylistAdapter adapter;
    private List<Stylist> stylistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stylist);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        stylistList = new ArrayList<>();

        adapter = new StylistAdapter(stylistList, this::openStylistProfile);
        recyclerView.setAdapter(adapter);

        loadStylistsFromFirebase();
    }

    private void loadStylistsFromFirebase() {
        DatabaseReference stylistsRef = FirebaseDatabase.getInstance().getReference("Stylists");
        stylistsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stylistList.clear();
                for (DataSnapshot stylistSnapshot : snapshot.getChildren()) {
                    Stylist stylist = stylistSnapshot.getValue(Stylist.class);
                    if (stylist != null) {
                        stylistList.add(stylist);
                        stylist.setFirebaseKey(stylistSnapshot.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageStylist.this, "Failed to load stylists: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void openStylistProfile(Stylist stylist) {
        Intent intent = new Intent(this, EditStylist.class);
        intent.putExtra("stylist_key", stylist.getFirebaseKey());
        intent.putExtra("stylist_name", stylist.getName());
        intent.putExtra("stylist_photo", stylist.getPhoto());
        intent.putExtra("stylist_experience", stylist.getYearsOfExperience());
        intent.putExtra("stylist_rating", stylist.getRating());
        intent.putExtra("stylist_services", stylist.getServices());
        startActivity(intent);
    }


    public void openAddStylist(View view) {
        Intent intent = new Intent(ManageStylist.this, AddStylist.class);
        startActivity(intent);
    }

    public void openAdminMainPage(View view) {
        Intent intent = new Intent(ManageStylist.this, AdminMainPageActivity.class);
        startActivity(intent);
    }

    public void manageService(View view) {
        Intent intent = new Intent(ManageStylist.this, ManageServiceList.class);
        startActivity(intent);
    }

    public void manageStylist(View view) {
        Intent intent = new Intent(ManageStylist.this, ManageStylist.class);
        startActivity(intent);
    }

    public void manageAppointment(View view) {
        Intent intent = new Intent(ManageStylist.this, ManageAppointment.class);
        startActivity(intent);
    }

    public void openAdminNotification(View view) {
        Intent intent = new Intent(ManageStylist.this, AdminNotificationActivity.class);
        startActivity(intent);
    }

    public void openAdminProfile(View view) {
        Intent intent = new Intent(ManageStylist.this, AdminProfile.class);
        startActivity(intent);
    }
}
