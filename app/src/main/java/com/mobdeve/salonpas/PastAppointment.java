package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PastAppointment extends AppCompatActivity {

    private TextView appointmentDateTextView;
    private TextView appointmentServicesTextView;
    private TextView appointmentStylistTextView;
    private RatingBar ratingBar;
    private EditText reviewEditText;
    private Button submitReviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_appointment);

        String appointmentDate = getIntent().getStringExtra("appointment_date");
        String appointmentServices = getIntent().getStringExtra("appointment_services");
        String appointmentStylist = getIntent().getStringExtra("appointment_stylist");

        appointmentDateTextView = findViewById(R.id.appointmentDateTextView);
        appointmentServicesTextView = findViewById(R.id.appointmentServicesTextView);
        appointmentStylistTextView = findViewById(R.id.appointmentStylistTextView);
        ratingBar = findViewById(R.id.ratingBar);
        reviewEditText = findViewById(R.id.reviewEditText);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        appointmentDateTextView.setText(appointmentDate);
        appointmentServicesTextView.setText(appointmentServices);
        appointmentStylistTextView.setText(appointmentStylist);

        submitReviewButton.setOnClickListener(v -> submitReview());
    }

    private void submitReview() {
        float rating = ratingBar.getRating();
        String review = reviewEditText.getText().toString();

        Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AppointmentHistory.class);
        startActivity(intent);
        finish();
    }
}
