package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileImage;
    private TextView valueName, valueBirthday, valueGender, valueContact, valueEmail;
    private Button editProfileButton, logoutButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        String currentUserId = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(currentUserId);

        initializeUI();
        loadUserData();

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void initializeUI() {
        profileImage = findViewById(R.id.profileImage);
        valueName = findViewById(R.id.valueName);
        valueBirthday = findViewById(R.id.valueBirthday);
        valueGender = findViewById(R.id.valueGender);
        valueContact = findViewById(R.id.valueContact);
        valueEmail = findViewById(R.id.valueEmail);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void loadUserData() {
        mDatabase.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    valueName.setText(user.getFirstName() + " " + user.getLastName());
                    valueBirthday.setText(user.getBirthdate() != null ? user.getBirthdate() : "Not provided");
                    valueGender.setText(user.getGender() != null ? user.getGender() : "Not provided");
                    valueContact.setText(user.getContact() != null ? user.getContact() : "Not provided");
                    valueEmail.setText(user.getEmail());

                    if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                        Glide.with(this)
                                .load(user.getProfilePictureUrl())
                                .placeholder(R.drawable.ic_default_profile)
                                .error(R.drawable.ic_error_image)
                                .into(profileImage);
                    } else {
                        profileImage.setImageResource(R.drawable.ic_default_profile);
                    }
                } else {
                    Toast.makeText(this, "User data is null.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to fetch data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void openAppointmentHistory(View view) {
        Intent intent = new Intent(ProfileActivity.this, AppointmentHistory.class);
        startActivity(intent);
    }

    public void openUserMainPage(View view) {
        Intent intent = new Intent(ProfileActivity.this, UserMainPageActivity.class);
        startActivity(intent);
    }

    public void openServicesList(View view) {
        Intent intent = new Intent(ProfileActivity.this, ServiceList.class);
        startActivity(intent);
    }

    public void openStylistList(View view) {
        Intent intent = new Intent(ProfileActivity.this, ViewStylistList.class);
        startActivity(intent);
    }

    public void openReservationPage(View view) {
        Intent intent = new Intent(ProfileActivity.this, AppointmentReservationActivity.class);
        startActivity(intent);
    }

    public void openNotificationPage(View view) {
        Intent intent = new Intent(ProfileActivity.this, AppointmentNotificationActivity.class);
        startActivity(intent);
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
