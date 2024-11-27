package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ReviewActivity extends AppCompatActivity {

    private RatingBar serviceRatingBar, stylistRatingBar;
    private EditText reviewCommentsEditText;
    private Button submitReviewButton;
    private DatabaseReference reviewsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        serviceRatingBar = findViewById(R.id.service_rating);
        stylistRatingBar = findViewById(R.id.stylist_rating);
        reviewCommentsEditText = findViewById(R.id.review_comments);
        submitReviewButton = findViewById(R.id.submit_review_button);

        reviewsRef = FirebaseDatabase.getInstance().getReference("Reviews");

        String appointmentId = getIntent().getStringExtra("appointment_id");
        String serviceName = getIntent().getStringExtra("service_name");
        String stylistName = getIntent().getStringExtra("stylist_name");

        submitReviewButton.setOnClickListener(v -> submitReview(appointmentId, serviceName, stylistName));
    }

    private void submitReview(String appointmentId, String serviceName, String stylistName) {
        float serviceRating = serviceRatingBar.getRating();
        float stylistRating = stylistRatingBar.getRating();
        String comments = reviewCommentsEditText.getText().toString();

        if (serviceRating == 0 || stylistRating == 0) {
            Toast.makeText(this, "Please rate both service and stylist.", Toast.LENGTH_SHORT).show();
            return;
        }

        Review review = new Review(appointmentId, serviceName, stylistName, serviceRating, stylistRating, comments);
        reviewsRef.child(appointmentId).setValue(review)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to submit review: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void openUserMainPage(View view) {
        Intent intent = new Intent(ReviewActivity.this, UserMainPageActivity.class);
        startActivity(intent);
    }

    public void openServicesList(View view) {
        Intent intent = new Intent(ReviewActivity.this, ServiceList.class);
        startActivity(intent);
    }

    public void openStylistList(View view) {
        Intent intent = new Intent(ReviewActivity.this, ViewStylistList.class);
        startActivity(intent);
    }

    public void openReservationPage (View view) {
        Intent intent = new Intent(ReviewActivity.this, AppointmentReservationActivity.class);
        startActivity(intent);
    }

    public void openNotificationPage (View view) {
        Intent intent = new Intent(ReviewActivity.this, AppointmentNotificationActivity.class);
        startActivity(intent);
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(ReviewActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
