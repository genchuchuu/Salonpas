package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AdminEditProfile extends AppCompatActivity {
    private EditText editName, editBirthday, editContact, editEmail;
    private RadioGroup radioGroupGender;
    private Button saveProfileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_profile);

        editName = findViewById(R.id.editName);
        editBirthday = findViewById(R.id.editBirthday);
        editContact = findViewById(R.id.editContact);
        editEmail = findViewById(R.id.editEmail);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        saveProfileButton = findViewById(R.id.saveProfileButton);

        loadUserData();

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private void loadUserData() {
        String name = "Kang Seulgi";
        String birthday = "01/01/1991";
        String contact = "1234567890";
        String email = "k.seulgi@gmail.com";
        String gender = "Female";

        editName.setText(name);
        editBirthday.setText(birthday);
        editContact.setText(contact);
        editEmail.setText(email);

        if (gender.equals("Female")) {
            ((RadioButton) findViewById(R.id.radioFemale)).setChecked(true);
        } else {
            ((RadioButton) findViewById(R.id.radioMale)).setChecked(true);
        }
    }

    private void saveProfile() {
        String name = editName.getText().toString();
        String birthday = editBirthday.getText().toString();
        String contact = editContact.getText().toString();
        String email = editEmail.getText().toString();

        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        String gender = selectedId == R.id.radioFemale ? "Female" : "Male";



        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();


        Intent intent = new Intent(AdminEditProfile.this, AdminProfile.class);
        startActivity(intent);
        finish();
    }
}
