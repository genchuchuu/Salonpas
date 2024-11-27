package com.mobdeve.salonpas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditAppointment extends AppCompatActivity {

    private TextView appointmentDateTextView;
    private TextView appointmentServicesTextView;
    private TextView appointmentStylistTextView;

    private String appointmentDate;
    private String appointmentServices;
    private String appointmentStylist;

    private Button completeButton;
    private Button cancelButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_appointment);

        appointmentDate = getIntent().getStringExtra("appointment_date");
        appointmentServices = getIntent().getStringExtra("appointment_services");
        appointmentStylist = getIntent().getStringExtra("appointment_stylist");
        completeButton = findViewById(R.id.completeButton);
        cancelButton = findViewById(R.id.cancelButton);

        appointmentDateTextView = findViewById(R.id.appointmentDateTextView);
        appointmentServicesTextView = findViewById(R.id.appointmentServicesTextView);
        appointmentStylistTextView = findViewById(R.id.appointmentStylistTextView);

        updateViews();

        setEditableOnClick(appointmentDateTextView, true);
        setEditableOnClick(appointmentServicesTextView, true);
        setEditableOnClick(appointmentStylistTextView, true);

        completeButton.setOnClickListener(v -> showCompleteDialog());
        cancelButton.setOnClickListener(v -> showCanceledDialog());
    }

    private void updateViews() {
        appointmentDateTextView.setText(appointmentDate);
        appointmentServicesTextView.setText(appointmentServices);
        appointmentStylistTextView.setText(appointmentStylist);
    }

    private void setEditableOnClick(TextView textView, boolean isEditable) {
        textView.setOnClickListener(v -> {

            EditText editText = new EditText(EditAppointment.this);
            editText.setText(textView.getText().toString());
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            ViewGroup parent = (ViewGroup) textView.getParent();
            if (parent != null) {

                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                editText.setLayoutParams(params);

                int index = parent.indexOfChild(textView);
                parent.addView(editText, index);
                textView.setVisibility(View.GONE);

                editText.requestFocus();

                editText.setOnFocusChangeListener((v1, hasFocus) -> {
                    if (!hasFocus) {

                        if (isEditable) {
                            if (textView == appointmentDateTextView) {
                                appointmentDate = editText.getText().toString();
                            } else if (textView == appointmentServicesTextView) {
                                appointmentServices = editText.getText().toString();
                            } else if (textView == appointmentStylistTextView) {
                                appointmentStylist = editText.getText().toString();
                            }
                        }

                        updateViews();

                        parent.removeView(editText);
                        textView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void showCompleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditAppointment.this);
        builder.setTitle("Appointment Completed");
        builder.setMessage("The appointment has been successfully completed.");

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

    private void showCanceledDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditAppointment.this);
        builder.setTitle("Appointment Canceled");
        builder.setMessage("The appointment has been canceled.");

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
}
