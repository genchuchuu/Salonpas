package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView createAccountLink;
    private FirebaseAuth mAuth;

    private final String ADMIN_EMAIL = "admin1@salonpas.store.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginPageButton);
        createAccountLink = findViewById(R.id.createAccount);

        mAuth = FirebaseAuth.getInstance();

        createAccountLink.setOnClickListener(view -> startActivity(new Intent(this, RegistrationActivity.class)));

        loginButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (validateInputs(email, password)) {
                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (email.equalsIgnoreCase(ADMIN_EMAIL)) {
                    startActivity(new Intent(this, AdminMainPageActivity.class));
                } else {
                    startActivity(new Intent(this, UserMainPageActivity.class));
                }
                finish();
            } else {
                Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs(String email, String password) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Enter a valid email!");
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is required!");
            return false;
        }
        return true;
    }
}
