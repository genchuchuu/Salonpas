package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageAppointment extends AppCompatActivity {

    private List<Appointment> appointments;
    private List<String> appointmentIds;
    private RecyclerView recyclerView;
    private AppointmentAdapter adapter;
    private DatabaseReference databaseReference;

    private Map<String, String> serviceNames = new HashMap<>();
    private Map<String, String> stylistNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_appointment);

        recyclerView = findViewById(R.id.appointmentRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        appointments = new ArrayList<>();
        appointmentIds = new ArrayList<>();

        // Fixed Adapter Initialization
        adapter = new AppointmentAdapter(appointments, appointmentIds, (appointmentId, serviceName, stylistName) -> {
            // No action required for admin side
        });

        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        fetchAppointments();
    }

    private void fetchAppointments() {
        fetchServicesAndStylists(this::fetchReservations);
    }

    private void fetchServicesAndStylists(Runnable onComplete) {
        databaseReference.child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                for (DataSnapshot service : serviceSnapshot.getChildren()) {
                    serviceNames.put(service.getKey(), service.child("name").getValue(String.class));
                }

                databaseReference.child("Stylists").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot stylistSnapshot) {
                        for (DataSnapshot stylist : stylistSnapshot.getChildren()) {
                            stylistNames.put(stylist.getKey(), stylist.child("name").getValue(String.class));
                        }
                        onComplete.run();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ManageAppointment", "Error fetching stylists", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ManageAppointment", "Error fetching services", error.toException());
            }
        });
    }

    private void fetchReservations() {
        databaseReference.child("Reservations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot reservationSnapshot) {
                appointments.clear();
                appointmentIds.clear();

                for (DataSnapshot reservation : reservationSnapshot.getChildren()) {
                    String appointmentId = reservation.getKey();
                    String date = reservation.child("date").getValue(String.class);
                    String time = reservation.child("time").getValue(String.class);
                    String serviceId = reservation.child("serviceId").getValue(String.class);
                    String stylistId = reservation.child("stylistId").getValue(String.class);
                    String userId = reservation.child("userId").getValue(String.class);

                    String serviceName = serviceNames.getOrDefault(serviceId, "Unknown Service");
                    String stylistName = stylistNames.getOrDefault(stylistId, "Unknown Stylist");

                    String fullDateTime = date + " at " + time;

                    appointments.add(new Appointment(fullDateTime, serviceName, stylistName, userId));
                    appointmentIds.add(appointmentId);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ManageAppointment", "Error fetching reservations", error.toException());
            }
        });
    }

    public void openAdminMainPage(View view) {
        Intent intent = new Intent(ManageAppointment.this, AdminMainPageActivity.class);
        startActivity(intent);
    }

    public void manageService(View view) {
        Intent intent = new Intent(ManageAppointment.this, ManageServiceList.class);
        startActivity(intent);
    }

    public void manageStylist(View view) {
        Intent intent = new Intent(ManageAppointment.this, ManageStylist.class);
        startActivity(intent);
    }

    public void manageAppointment(View view) {
        Intent intent = new Intent(ManageAppointment.this, ManageAppointment.class);
        startActivity(intent);
    }

    public void openAdminNotification(View view) {
        Intent intent = new Intent(ManageAppointment.this, AdminNotificationActivity.class);
        startActivity(intent);
    }

    public void openAdminProfile(View view) {
        Intent intent = new Intent(ManageAppointment.this, AdminProfile.class);
        startActivity(intent);
    }
}
