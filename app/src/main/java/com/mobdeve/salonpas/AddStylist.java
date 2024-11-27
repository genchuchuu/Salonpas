package com.mobdeve.salonpas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddStylist extends AppCompatActivity {
    private Button submitStylist;
    private EditText editTextStylistName, editTextServicesOffered, editTextExperience;
    private ImageView imageViewStylistPhoto;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stylist);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        submitStylist = findViewById(R.id.submitStylist);
        editTextStylistName = findViewById(R.id.stylistNameField);
        editTextServicesOffered = findViewById(R.id.servicesField);
        editTextExperience = findViewById(R.id.experienceField);
        imageViewStylistPhoto = findViewById(R.id.stylistImage);

        submitStylist.setOnClickListener(v -> addStylistToDatabase());
    }

    private void addStylistToDatabase() {
        String stylistName = editTextStylistName.getText().toString().trim();
        String servicesOffered = editTextServicesOffered.getText().toString().trim();
        String experience = editTextExperience.getText().toString().trim();

        if (stylistName.isEmpty() || servicesOffered.isEmpty() || experience.isEmpty()) {
            Toast.makeText(AddStylist.this, "Please provide all details.", Toast.LENGTH_SHORT).show();
            return;
        }

        int yearsOfExperience = 0;
        try {
            yearsOfExperience = Integer.parseInt(experience);  // Convert the experience to int
        } catch (NumberFormatException e) {
            Toast.makeText(AddStylist.this, "Invalid experience input. Please enter a valid number.", Toast.LENGTH_SHORT).show();
            return;
        }

        String photoUrl = "''";

        float rating = 0.0f;

        Map<String, Object> stylist = new HashMap<>();
        stylist.put("name", stylistName);
        stylist.put("services", servicesOffered);
        stylist.put("yearsOfExperience", yearsOfExperience);
        stylist.put("photo", photoUrl);
        stylist.put("rating", rating);

        databaseReference.child("Stylists").push().setValue(stylist)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddStylist.this, "Stylist added successfully.", Toast.LENGTH_SHORT).show();
                    showCompleteDialog();  // Show the completion dialog
                })
                .addOnFailureListener(e -> Toast.makeText(AddStylist.this, "Failed to add stylist.", Toast.LENGTH_SHORT).show());
    }

    private void showCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddStylist.this);
        builder.setTitle("Stylist Added");
        builder.setMessage("The stylist has been successfully added.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openAdminMainPage(View view) {
        Intent intent = new Intent(AddStylist.this, AdminMainPageActivity.class);
        startActivity(intent);
    }

    public void manageService(View view) {
        Intent intent = new Intent(AddStylist.this, ManageServiceList.class);
        startActivity(intent);
    }

    public void manageStylist(View view) {
        Intent intent = new Intent(AddStylist.this, ManageStylist.class);
        startActivity(intent);
    }

    public void manageAppointment(View view) {
        Intent intent = new Intent(AddStylist.this, ManageAppointment.class);
        startActivity(intent);
    }

    public void openAdminProfile(View view) {
        Intent intent = new Intent(AddStylist.this, AdminProfile.class);
        startActivity(intent);
    }
}
