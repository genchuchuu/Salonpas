package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditStylist extends AppCompatActivity {
    private TextView nameTextView;
    private TextView ratingTextView;
    private TextView experienceTextView;
    private TextView servicesTextView;
    private ImageView photoImageView;

    private String firebaseKey; // Firebase key
    private String name;
    private double rating;
    private int years;
    private String services;

    private DatabaseReference stylistRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stylist);

        firebaseKey = getIntent().getStringExtra("stylist_key");
        name = getIntent().getStringExtra("stylist_name");
        String photo = getIntent().getStringExtra("stylist_photo");
        years = getIntent().getIntExtra("stylist_experience", 0);
        rating = getIntent().getDoubleExtra("stylist_rating", 0);
        services = getIntent().getStringExtra("stylist_services");

        stylistRef = FirebaseDatabase.getInstance().getReference("Stylists").child(firebaseKey);

        nameTextView = findViewById(R.id.stylistName);
        photoImageView = findViewById(R.id.stylistImage);
        ratingTextView = findViewById(R.id.stylistRating);
        experienceTextView = findViewById(R.id.stylistExperience);
        servicesTextView = findViewById(R.id.stylistServices);

        updateViews();

        int photoResId = getResources().getIdentifier(photo, "drawable", getPackageName());
        photoImageView.setImageResource(photoResId);

        setEditableOnClick(nameTextView, "name");
        setEditableOnClick(ratingTextView, "rating");
        setEditableOnClick(experienceTextView, "yearsOfExperience");
        setEditableOnClick(servicesTextView, "services");
    }

    private void updateViews() {
        nameTextView.setText(name);
        ratingTextView.setText(String.format("%.1f", rating));
        experienceTextView.setText(String.format("%d", years));
        servicesTextView.setText(services);
    }

    private void setEditableOnClick(TextView textView, String fieldKey) {
        textView.setOnClickListener(v -> {
            EditText editText = new EditText(EditStylist.this);
            editText.setText(textView.getText().toString());

            if (fieldKey.equals("rating")) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            } else if (fieldKey.equals("yearsOfExperience")) {
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
            } else {
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }
            editText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

            ViewGroup parent = (ViewGroup) textView.getParent();
            if (parent != null) {
                int index = parent.indexOfChild(textView);
                parent.addView(editText, index);
                textView.setVisibility(View.GONE);

                editText.requestFocus();
                editText.setOnFocusChangeListener((v1, hasFocus) -> {
                    if (!hasFocus) {
                        String newValue = editText.getText().toString().trim();
                        Object parsedValue = null;

                        try {
                            if (fieldKey.equals("name")) {
                                name = newValue;
                                parsedValue = newValue;
                            } else if (fieldKey.equals("services")) {
                                services = newValue;
                                parsedValue = newValue;
                            } else if (fieldKey.equals("rating")) {
                                parsedValue = Double.parseDouble(newValue);
                                rating = (double) parsedValue;
                            } else if (fieldKey.equals("yearsOfExperience")) {
                                parsedValue = Integer.parseInt(newValue);
                                years = (int) parsedValue;
                            }

                            stylistRef.child(fieldKey).setValue(parsedValue)
                                    .addOnSuccessListener(aVoid -> {
                                        textView.setText(newValue); // Update TextView with the new value
                                        parent.removeView(editText);
                                        textView.setVisibility(View.VISIBLE);
                                        showValueSavedDialog(); // Show success dialog
                                    })
                                    .addOnFailureListener(e -> {
                                        parent.removeView(editText);
                                        textView.setVisibility(View.VISIBLE);
                                        Toast.makeText(EditStylist.this, "Update failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });
                        } catch (NumberFormatException e) {
                            Toast.makeText(EditStylist.this, "Invalid input for " + fieldKey + ". Please enter a valid value.", Toast.LENGTH_LONG).show();
                            parent.removeView(editText);
                            textView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }



    private void showValueSavedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Success")
                .setMessage("Value saved successfully!")
                .setPositiveButton("OK", null)
                .setCancelable(false)
                .show();
    }


    private void restartEditStylist() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    public void openAdminMainPage(View view) {
        Intent intent = new Intent(EditStylist.this, AdminMainPageActivity.class);
        startActivity(intent);
    }

    public void manageService(View view) {
        Intent intent = new Intent(EditStylist.this, ManageServiceList.class);
        startActivity(intent);
    }

    public void manageStylist(View view) {
        Intent intent = new Intent(EditStylist.this, ManageStylist.class);
        startActivity(intent);
    }

    public void manageAppointment(View view) {
        Intent intent = new Intent(EditStylist.this, ManageAppointment.class);
        startActivity(intent);
    }

    public void onDeleteStylistClicked(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Deletion")
                .setMessage("Are you sure you want to delete this stylist?")
                .setPositiveButton("Yes", (dialog, which) -> deleteStylist())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteStylist() {
        stylistRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(EditStylist.this, "Stylist deleted successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(EditStylist.this, "Failed to delete stylist. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
