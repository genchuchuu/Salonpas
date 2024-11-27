package com.mobdeve.salonpas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editFirstName, editLastName, editBirthday, editContact;
    private ImageView profileImage;
    private Button saveProfileButton, cancelProfileButton;
    private Uri imageUri;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize UI elements
        profileImage = findViewById(R.id.profileImage);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editBirthday = findViewById(R.id.editBirthday);
        editContact = findViewById(R.id.editContact);
        saveProfileButton = findViewById(R.id.saveProfileButton);
        cancelProfileButton = findViewById(R.id.cancelProfileButton);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
        mStorage = FirebaseStorage.getInstance().getReference("ProfilePictures");

        loadUserData();

        profileImage.setOnClickListener(v -> openFileChooser());
        saveProfileButton.setOnClickListener(v -> saveProfile());
        cancelProfileButton.setOnClickListener(v -> finish());
        editBirthday.setOnClickListener(v -> openDatePicker());

        Button deleteProfileButton = findViewById(R.id.deleteProfileButton);
        deleteProfileButton.setOnClickListener(v -> deleteProfile());

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
        }
    }

    private void loadUserData() {
        mDatabase.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DataSnapshot snapshot = task.getResult();
                User user = snapshot.getValue(User.class);
                if (user != null) {
                    editFirstName.setText(user.getFirstName() != null ? user.getFirstName() : "");
                    editLastName.setText(user.getLastName() != null ? user.getLastName() : "");
                    editBirthday.setText(user.getBirthdate() != null ? user.getBirthdate() : "");
                    editContact.setText(user.getContact() != null ? user.getContact() : "");

                    // Always use the default profile image
                    profileImage.setImageResource(R.drawable.ic_default_profile);
                } else {
                    showToast("User data is missing.");
                }
            } else {
                showToast("Failed to load user data.");
            }
        }).addOnFailureListener(e -> showToast("Error: " + e.getMessage()));
    }


    private void saveProfile() {
        String firstName = editFirstName.getText().toString().trim();
        String lastName = editLastName.getText().toString().trim();
        String birthdate = editBirthday.getText().toString().trim();
        String contact = editContact.getText().toString().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || birthdate.isEmpty() || contact.isEmpty()) {
            showToast("All fields are required.");
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Saving profile...");
        progressDialog.show();

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("firstName", firstName);
        updates.put("lastName", lastName);
        updates.put("birthdate", birthdate);
        updates.put("contact", contact);

        mDatabase.updateChildren(updates).addOnCompleteListener(task -> {
            progressDialog.dismiss();
            if (task.isSuccessful()) {
                showToast("Profile updated successfully!");
                finish();
            } else {
                showToast("Failed to update profile.");
            }
        });
    }


    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> editBirthday.setText((month + 1) + "/" + dayOfMonth + "/" + year),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deleteProfile() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete your profile?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Deleting profile...");
                    progressDialog.show();

                    deleteAppointmentsAndReservations(mAuth.getCurrentUser().getUid(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                mDatabase.removeValue().addOnCompleteListener(databaseTask -> {
                                    if (databaseTask.isSuccessful()) {
                                        mAuth.getCurrentUser().delete().addOnCompleteListener(authTask -> {
                                            if (authTask.isSuccessful()) {
                                                showToast("Profile deleted successfully!");
                                                redirectToLogin();
                                            } else {
                                                showToast("Failed to delete user from authentication.");
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        showToast("Failed to delete profile data.");
                                    }
                                });
                            } else {
                                progressDialog.dismiss();
                                showToast("Failed to delete appointments and reservations.");
                            }
                        }
                    });
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss())
                .show();
    }

    // Helper method to delete appointments/reservations
    private void deleteAppointmentsAndReservations(String userId, OnCompleteListener<Void> listener) {
        mDatabase.child("appointments").child(userId).removeValue()
                .addOnCompleteListener(listener);
    }


    private void redirectToLogin() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);

        finish();
    }


}
