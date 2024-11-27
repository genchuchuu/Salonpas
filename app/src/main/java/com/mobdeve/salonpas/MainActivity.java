package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);

        testFirebaseDatabaseConnection();

        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        signupButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

    }

    private void testFirebaseDatabaseConnection() {
        DatabaseReference testRef = FirebaseDatabase.getInstance().getReference("testConnection");
        testRef.setValue("Hello, Firebase!")
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Firebase database connection successful!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Firebase connection failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}
