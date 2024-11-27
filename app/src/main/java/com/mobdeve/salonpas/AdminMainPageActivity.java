package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminMainPageActivity extends AppCompatActivity {

    private TextView greetingTextView;
    private FirebaseAuth mAuth;
    private static final String ADMIN_EMAIL = "admin1@salonpas.store.com"; // Hardcoded admin email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adminmainpage);

        greetingTextView = findViewById(R.id.greeting);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            validateAdminAccess(currentUser.getEmail());
        } else {
            redirectToLogin("Unauthorized access. Please log in.");
        }
    }

    private void validateAdminAccess(String email) {
        if (email != null && email.equals(ADMIN_EMAIL)) {
            greetingTextView.setText("Welcome, Admin!");
        } else {
            redirectToLogin("Access denied. You are not authorized to view this page.");
        }
    }

    private void redirectToLogin(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AdminMainPageActivity.this, MainActivity.class); // Redirect to login screen
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void manageService(View view) {
        Intent intent = new Intent(AdminMainPageActivity.this, ManageServiceList.class);
        startActivity(intent);
    }

    public void manageStylist(View view) {
        Intent intent = new Intent(AdminMainPageActivity.this, ManageStylist.class);
        startActivity(intent);
    }

    public void manageAppointment(View view) {
        Intent intent = new Intent(AdminMainPageActivity.this, ManageAppointment.class);
        startActivity(intent);
    }

    public void openAdminNotification(View view) {
        Intent intent = new Intent(AdminMainPageActivity.this, AdminNotificationActivity.class);
        startActivity(intent);
    }

    public void openAdminProfile(View view) {
        Intent intent = new Intent(AdminMainPageActivity.this, AdminProfile.class);
        startActivity(intent);
    }
}
