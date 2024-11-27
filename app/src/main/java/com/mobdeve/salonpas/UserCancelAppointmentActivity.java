package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserCancelAppointmentActivity extends AppCompatActivity {

    private String appointmentId;
    private String appointmentDate;
    private String appointmentServices;
    private String appointmentStylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cancel_appointment);

        appointmentId = getIntent().getStringExtra("appointment_id");
        appointmentDate = getIntent().getStringExtra("appointment_date");
        appointmentServices = getIntent().getStringExtra("appointment_services");
        appointmentStylist = getIntent().getStringExtra("appointment_stylist");

        TextView appointmentDetails = findViewById(R.id.appointmentDetails);
        appointmentDetails.setText(
                "Date: " + appointmentDate + "\n" +
                        "Services: " + appointmentServices + "\n" +
                        "Stylist: " + appointmentStylist
        );

        findViewById(R.id.btnCancelAppointment).setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Appointment")
                    .setMessage("Are you sure you want to cancel this appointment?")
                    .setPositiveButton("Yes", (dialog, which) -> cancelAppointment())
                    .setNegativeButton("No", null)
                    .show();
        });
    }

    private void cancelAppointment() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Reservations").child(appointmentId);
        dbRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Appointment canceled successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to cancel appointment.", Toast.LENGTH_SHORT).show());
    }

    public void openUserMainPage(View view) {
        Intent intent = new Intent(UserCancelAppointmentActivity.this, UserMainPageActivity.class);
        startActivity(intent);
    }

    public void openServicesList(View view) {
        Intent intent = new Intent(UserCancelAppointmentActivity.this, ServiceList.class);
        startActivity(intent);
    }

    public void openStylistList(View view) {
        Intent intent = new Intent(UserCancelAppointmentActivity.this, ViewStylistList.class);
        startActivity(intent);
    }

    public void openReservationPage(View view) {
        Intent intent = new Intent(UserCancelAppointmentActivity.this, AppointmentReservationActivity.class);
        startActivity(intent);
    }

    public void openNotificationPage(View view) {
        Intent intent = new Intent(UserCancelAppointmentActivity.this, AppointmentNotificationActivity.class);
        startActivity(intent);
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(UserCancelAppointmentActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
