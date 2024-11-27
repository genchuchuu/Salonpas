package com.mobdeve.salonpas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class UserEditAppointmentActivity extends AppCompatActivity {

    private EditText editDate, editTime;
    private Spinner spinnerServices, spinnerStylist;

    private void loadServices() {
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("Services");
        servicesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> serviceNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String serviceName = snapshot.child("name").getValue(String.class);
                    if (serviceName != null) {
                        serviceNames.add(serviceName);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UserEditAppointmentActivity.this,
                        android.R.layout.simple_spinner_item, serviceNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerServices.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserEditAppointmentActivity.this, "Failed to load services.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStylists() {
        DatabaseReference stylistsRef = FirebaseDatabase.getInstance().getReference("Stylists");
        stylistsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> stylistNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String stylistName = snapshot.child("name").getValue(String.class);
                    if (stylistName != null) {
                        stylistNames.add(stylistName);
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(UserEditAppointmentActivity.this,
                        android.R.layout.simple_spinner_item, stylistNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStylist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserEditAppointmentActivity.this, "Failed to load stylists.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_appointment);

        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTime);
        spinnerServices = findViewById(R.id.spinnerServices);
        spinnerStylist = findViewById(R.id.spinnerStylist);

        editDate.setText(getIntent().getStringExtra("appointment_date"));
        editTime.setText(getIntent().getStringExtra("appointment_time"));

        loadServices();
        loadStylists();

        editDate.setOnClickListener(view -> openDatePicker());
        editTime.setOnClickListener(view -> openTimePicker());
    }

    public void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = (month + 1) + "/" + dayOfMonth + "/" + year;
                    editDate.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    public void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute) -> {
                    String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
                    editTime.setText(selectedTime);
                },
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    public void saveChanges(View view) {
        String updatedDate = editDate.getText().toString().trim();
        String updatedTime = editTime.getText().toString().trim();
        String updatedServices = spinnerServices.getSelectedItem().toString();
        String updatedStylist = spinnerStylist.getSelectedItem().toString();
        String appointmentId = getIntent().getStringExtra("appointment_id");

        if (updatedDate.isEmpty() && updatedTime.isEmpty() && updatedServices.isEmpty() && updatedStylist.isEmpty()) {
            Toast.makeText(this, "No changes made.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference("Reservations").child(appointmentId);

        if (!updatedDate.isEmpty()) {
            appointmentRef.child("date").setValue(updatedDate);
        }
        if (!updatedTime.isEmpty()) {
            appointmentRef.child("time").setValue(updatedTime);
        }
        if (!updatedServices.isEmpty()) {
            getServiceIdByName(updatedServices, serviceId -> {
                if (!serviceId.isEmpty()) {
                    appointmentRef.child("serviceId").setValue(serviceId);
                } else {
                    Toast.makeText(this, "Invalid service name.", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (!updatedStylist.isEmpty()) {
            getStylistIdByName(updatedStylist, stylistId -> {
                if (!stylistId.isEmpty()) {
                    appointmentRef.child("stylistId").setValue(stylistId);
                } else {
                    Toast.makeText(this, "Invalid stylist name.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Toast.makeText(this, "Appointment updated successfully.", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void getServiceIdByName(String serviceName, FirebaseCallback callback) {
        DatabaseReference servicesRef = FirebaseDatabase.getInstance().getReference("Services");
        servicesRef.orderByChild("name").equalTo(serviceName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        callback.onCallback(childSnapshot.getKey());
                        return;
                    }
                } else {
                    callback.onCallback("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserEditAppointmentActivity.this, "Failed to fetch service ID: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getStylistIdByName(String stylistName, FirebaseCallback callback) {
        DatabaseReference stylistsRef = FirebaseDatabase.getInstance().getReference("Stylists");
        stylistsRef.orderByChild("name").equalTo(stylistName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        callback.onCallback(childSnapshot.getKey());
                        return;
                    }
                } else {
                    callback.onCallback("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserEditAppointmentActivity.this, "Failed to fetch stylist ID: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface FirebaseCallback {
        void onCallback(String id);
    }

    public void openUserMainPage(View view) {
        Intent intent = new Intent(UserEditAppointmentActivity.this, UserMainPageActivity.class);
        startActivity(intent);
    }

    public void openServicesList(View view) {
        Intent intent = new Intent(UserEditAppointmentActivity.this, ServiceList.class);
        startActivity(intent);
    }

    public void openStylistList(View view) {
        Intent intent = new Intent(UserEditAppointmentActivity.this, ViewStylistList.class);
        startActivity(intent);
    }

    public void openReservationPage(View view) {
        Intent intent = new Intent(UserEditAppointmentActivity.this, AppointmentReservationActivity.class);
        startActivity(intent);
    }

    public void openNotificationPage(View view) {
        Intent intent = new Intent(UserEditAppointmentActivity.this, AppointmentNotificationActivity.class);
        startActivity(intent);
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(UserEditAppointmentActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
