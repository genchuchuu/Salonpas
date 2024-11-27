package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminProfile extends AppCompatActivity {

    private ImageView profileImage;
    private TextView valueContact, valueEmail;
    private Button editProfileButton, logoutAdminButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static final String ADMIN_EMAIL = "admin1@salonpas.store.com"; // Predefined admin email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        profileImage = findViewById(R.id.profileImage);
        valueContact = findViewById(R.id.valueContact);
        valueEmail = findViewById(R.id.valueEmail);
        editProfileButton = findViewById(R.id.editProfileButton);
        logoutAdminButton = findViewById(R.id.logoutAdminButton);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        loadAdminData();

        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfile.this, AdminEditProfile.class);
            startActivity(intent);
        });

        logoutAdminButton.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(AdminProfile.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadAdminData() {
        mDatabase.orderByChild("email").equalTo(ADMIN_EMAIL).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User adminUser = snapshot.getValue(User.class);
                        if (adminUser != null) {
                            valueEmail.setText(adminUser.getEmail());
                            valueContact.setText(adminUser.getContact());

                            if (adminUser.getProfilePictureUrl() != null && !adminUser.getProfilePictureUrl().isEmpty()) {
                                Glide.with(AdminProfile.this)
                                        .load(adminUser.getProfilePictureUrl())
                                        .placeholder(R.drawable.ic_default_profile)
                                        .error(R.drawable.ic_error_image)
                                        .into(profileImage);
                            } else {
                                profileImage.setImageResource(R.drawable.ic_default_profile);
                            }
                        } else {
                            displayError("Failed to load admin data.");
                        }
                    }
                } else {
                    displayError("Admin data not found in the database.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("AdminProfile", "Database error: " + databaseError.getMessage());
                displayError("Failed to fetch admin data.");
            }
        });
    }

    private void displayError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        valueEmail.setText("N/A");
        valueContact.setText("N/A");
        profileImage.setImageResource(R.drawable.ic_default_profile);
    }

    public void openAdminMainPage(View view) {
        Intent intent = new Intent(AdminProfile.this, AdminMainPageActivity.class);
        startActivity(intent);
    }

    public void manageService(View view) {
        Intent intent = new Intent(AdminProfile.this, ManageServiceList.class);
        startActivity(intent);
    }

    public void manageStylist(View view) {
        Intent intent = new Intent(AdminProfile.this, ManageStylist.class);
        startActivity(intent);
    }

    public void manageAppointment(View view) {
        Intent intent = new Intent(AdminProfile.this, ManageAppointment.class);
        startActivity(intent);
    }

    public void openAdminNotification(View view) {
        Intent intent = new Intent(AdminProfile.this, AdminNotificationActivity.class);
        startActivity(intent);
    }

    public void openAdminProfile(View view) {
        Intent intent = new Intent(AdminProfile.this, AdminProfile.class);
        startActivity(intent);
    }
}
