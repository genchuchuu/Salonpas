package com.mobdeve.salonpas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AppointmentReservationActivity extends AppCompatActivity {

    private Spinner spinnerServices, spinnerStylists;
    private Button btnSelectDate, btnSelectTime, btnConfirmBooking;
    private TextView textSelectedDate, textSelectedTime;

    private DatabaseReference databaseReference;
    private String selectedServiceId, selectedStylistId, selectedDate, selectedTime;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_reservation);

        spinnerServices = findViewById(R.id.spinner_services);
        spinnerStylists = findViewById(R.id.spinner_stylists);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectTime = findViewById(R.id.btn_select_time);
        btnConfirmBooking = findViewById(R.id.btn_confirm_booking);
        textSelectedDate = findViewById(R.id.text_selected_date);
        textSelectedTime = findViewById(R.id.text_selected_time);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadServices();

        btnSelectDate.setOnClickListener(v -> showDatePicker());
        btnSelectTime.setOnClickListener(v -> showTimePicker());
        btnConfirmBooking.setOnClickListener(v -> saveReservation());
    }

    private void loadServices() {
        databaseReference.child("Services").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> services = new ArrayList<>();
                Map<String, String> serviceMap = new HashMap<>();

                for (DataSnapshot serviceSnapshot : snapshot.getChildren()) {
                    String serviceName = serviceSnapshot.child("name").getValue(String.class);
                    String serviceId = serviceSnapshot.getKey();
                    services.add(serviceName);
                    serviceMap.put(serviceName, serviceId);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AppointmentReservationActivity.this, android.R.layout.simple_spinner_item, services);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerServices.setAdapter(adapter);

                spinnerServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedService = services.get(position);
                        selectedServiceId = serviceMap.get(selectedService);
                        loadStylists(selectedService);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        if (parent.getId() == R.id.spinner_services) {
                            selectedServiceId = null;
                            Toast.makeText(AppointmentReservationActivity.this, "No service selected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AppointmentReservationActivity.this, "Failed to load services.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStylists(String service) {
        databaseReference.child("Stylists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> stylists = new ArrayList<>();
                Map<String, String> stylistMap = new HashMap<>();

                for (DataSnapshot stylistSnapshot : snapshot.getChildren()) {
                    String stylistServices = stylistSnapshot.child("services").getValue(String.class);
                    if (stylistServices != null && stylistServices.contains(service)) {
                        String stylistName = stylistSnapshot.child("name").getValue(String.class);
                        String stylistId = stylistSnapshot.getKey();
                        stylists.add(stylistName);
                        stylistMap.put(stylistName, stylistId);
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(AppointmentReservationActivity.this, android.R.layout.simple_spinner_item, stylists);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStylists.setAdapter(adapter);

                spinnerStylists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String selectedStylist = stylists.get(position);
                        selectedStylistId = stylistMap.get(selectedStylist);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        if (parent.getId() == R.id.spinner_stylists) {
                            selectedStylistId = null;
                            Toast.makeText(AppointmentReservationActivity.this, "No stylist selected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AppointmentReservationActivity.this, "Failed to load stylists.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
            textSelectedDate.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            selectedTime = String.format("%02d:%02d", hourOfDay, minute);
            textSelectedTime.setText(selectedTime);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    private void saveReservation() {
        if (selectedServiceId == null || selectedStylistId == null || selectedDate == null || selectedTime == null) {
            Toast.makeText(this, "Please complete all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> reservation = new HashMap<>();
        reservation.put("userId", currentUserId);
        reservation.put("serviceId", selectedServiceId);
        reservation.put("stylistId", selectedStylistId);
        reservation.put("date", selectedDate);
        reservation.put("time", selectedTime);

        databaseReference.child("Reservations").push().setValue(reservation)
                .addOnSuccessListener(aVoid -> Toast.makeText(AppointmentReservationActivity.this, "Reservation saved successfully.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(AppointmentReservationActivity.this, "Failed to save reservation.", Toast.LENGTH_SHORT).show());
    }

    public void openUserMainPage(View view) {
        Intent intent = new Intent(AppointmentReservationActivity.this, UserMainPageActivity.class);
        startActivity(intent);
    }

    public void openServicesList(View view) {
        Intent intent = new Intent(AppointmentReservationActivity.this, ServiceList.class);
        startActivity(intent);
    }

    public void openStylistList(View view) {
        Intent intent = new Intent(AppointmentReservationActivity.this, ViewStylistList.class);
        startActivity(intent);
    }

    public void openReservationPage(View view) {
        Intent intent = new Intent(AppointmentReservationActivity.this, AppointmentReservationActivity.class);
        startActivity(intent);
    }

    public void openNotificationPage(View view) {
        Intent intent = new Intent(AppointmentReservationActivity.this, AppointmentNotificationActivity.class);
        startActivity(intent);
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(AppointmentReservationActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}