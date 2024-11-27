package com.mobdeve.salonpas;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, passwordInput, birthdateInput, contactInput;
    private Spinner genderSpinner;
    private Button registerButton;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView alreadyHaveAccount;

    private static final String ADMIN_EMAIL = "admin1@salonpas.store.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        birthdateInput = findViewById(R.id.birthdateInput);
        contactInput = findViewById(R.id.contactInput);
        genderSpinner = findViewById(R.id.genderSpinner);
        registerButton = findViewById(R.id.registerButton);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        setupGenderSpinner();
        birthdateInput.setOnClickListener(view -> showDatePickerDialog());
        alreadyHaveAccount.setOnClickListener(view -> startActivity(new Intent(this, LoginActivity.class)));

        registerButton.setOnClickListener(view -> {
            if (validateInputs()) {
                registerButton.setEnabled(false);
                registerUser();
            }
        });

        ensureAdminAccount();
    }

    private void setupGenderSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.gender_options, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, (view, year, month, day) -> birthdateInput.setText((month + 1) + "/" + day + "/" + year),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void registerUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String birthdate = birthdateInput.getText().toString().trim();
        String gender = genderSpinner.getSelectedItem().toString().trim();
        String contact = contactInput.getText().toString().trim();

        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().getSignInMethods().isEmpty()) {
                Toast.makeText(this, "Email is already in use. Please use another email.", Toast.LENGTH_SHORT).show();
                registerButton.setEnabled(true);
            } else {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
                    if (authTask.isSuccessful()) {
                        saveUserToDatabase(firstName, lastName, email, birthdate, gender, contact);
                    } else {
                        registerButton.setEnabled(true);
                        Toast.makeText(this, "Registration failed: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void saveUserToDatabase(String firstName, String lastName, String email, String birthdate, String gender, String contact) {
        String userId = mAuth.getCurrentUser().getUid();
        User userInfo = new User(firstName, lastName, email, birthdate, gender, contact, null); // Explicitly set profilePictureUrl to null

        mDatabase.child(userId).setValue(userInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                navigateToMainPage();
            } else {
                registerButton.setEnabled(true);
                Toast.makeText(this, "Failed to save user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ensureAdminAccount() {
        Log.d("AdminCheck", "ensureAdminAccount() called");

        mDatabase.orderByChild("email").equalTo(ADMIN_EMAIL).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().hasChildren()) {
                Log.d("AdminCheck", "Admin account already exists.");
            } else {
                mAuth.createUserWithEmailAndPassword(ADMIN_EMAIL, "admin123")
                        .addOnCompleteListener(adminTask -> {
                            if (adminTask.isSuccessful()) {
                                String adminId = mAuth.getCurrentUser().getUid();
                                User adminUser = new User("Admin", "User", ADMIN_EMAIL, "01/01/2000", "Other", "09154923678", null);

                                mDatabase.child(adminId).setValue(adminUser).addOnCompleteListener(addTask -> {
                                    if (addTask.isSuccessful()) {
                                        Log.d("AdminCheck", "Admin account added successfully.");
                                    } else {
                                        Log.e("AdminCheck", "Failed to add admin account: " + addTask.getException().getMessage());
                                    }
                                });
                            } else {
                                Log.e("AdminCheck", "Failed to create admin user: " + adminTask.getException().getMessage());
                            }
                        });
            }
        }).addOnFailureListener(e -> Log.e("AdminCheck", "Error checking for admin account: " + e.getMessage()));
    }

    private void navigateToMainPage() {
        Intent intent = new Intent(this, UserMainPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean validateInputs() {
        if (TextUtils.isEmpty(firstNameInput.getText().toString())) {
            firstNameInput.setError("First Name is required!");
            return false;
        }
        if (TextUtils.isEmpty(lastNameInput.getText().toString())) {
            lastNameInput.setError("Last Name is required!");
            return false;
        }
        if (TextUtils.isEmpty(emailInput.getText().toString()) || !Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) {
            emailInput.setError("Enter a valid email!");
            return false;
        }
        if (TextUtils.isEmpty(passwordInput.getText().toString()) || passwordInput.getText().toString().length() < 6) {
            passwordInput.setError("Password must be at least 6 characters!");
            return false;
        }
        if (TextUtils.isEmpty(birthdateInput.getText().toString())) {
            birthdateInput.setError("Birthdate is required!");
            return false;
        }
        if (genderSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a gender!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(contactInput.getText().toString()) || !contactInput.getText().toString().matches("^(\\+63|0)9\\d{9}$")) {
            contactInput.setError("Enter a valid Philippine phone number!");
            return false;
        }
        return true;
    }
}
