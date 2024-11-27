package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobdeve.salonpas.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointmentNotificationActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private String currentUserId;
    private RecyclerView notificationRecyclerView;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_notification);

        notificationRecyclerView = findViewById(R.id.recycler_notifications);
        notificationRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        notificationAdapter = new NotificationAdapter(notificationList);
        notificationRecyclerView.setAdapter(notificationAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        fetchUserReservations(currentUserId);
    }

    private void fetchUserReservations(String userId) {
        databaseReference.child("Reservations").orderByChild("userId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot reservationSnapshot : snapshot.getChildren()) {
                            processReservation(reservationSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error fetching reservations: " + error.getMessage());
                    }
                });
    }

    private void processReservation(DataSnapshot reservationSnapshot) {
        String date = reservationSnapshot.child("date").getValue(String.class);
        String time = reservationSnapshot.child("time").getValue(String.class);
        String serviceId = reservationSnapshot.child("serviceId").getValue(String.class);
        String stylistId = reservationSnapshot.child("stylistId").getValue(String.class);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date reservationDateTime = sdf.parse(date + " " + time);

            if (reservationDateTime != null) {
                Date now = new Date();
                long timeDiff = reservationDateTime.getTime() - now.getTime();

                // Check if the reservation is 30 minutes away or 24 hours after
                if (timeDiff <= 30 * 60 * 1000 && timeDiff > 0) {
                    fetchAndDisplayNotification(serviceId, stylistId, "30 minutes");
                } else if (timeDiff <= -24 * 60 * 60 * 1000 && timeDiff > -25 * 60 * 60 * 1000) {
                    fetchAndDisplayNotification(serviceId, stylistId, "24 hours");
                }
            }
        } catch (ParseException e) {
            Log.e("ParseError", "Error parsing reservation date/time: " + e.getMessage());
        }
    }

    private void fetchAndDisplayNotification(String serviceId, String stylistId, String timeContext) {
        databaseReference.child("Services").child(serviceId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot serviceSnapshot) {
                        String serviceName = serviceSnapshot.child("name").getValue(String.class);

                        databaseReference.child("Stylists").child(stylistId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot stylistSnapshot) {
                                        String stylistName = stylistSnapshot.child("name").getValue(String.class);
                                        createNotification(serviceName, stylistName, timeContext);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("FirebaseError", "Error fetching stylist: " + error.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseError", "Error fetching service: " + error.getMessage());
                    }
                });
    }

    private void createNotification(String serviceName, String stylistName, String timeContext) {
        String message;
        if (timeContext.equals("30 minutes")) {
            message = "Your appointment with " + stylistName + " is in 30 minutes.\nService: " + serviceName;
        } else {
            message = "Don't forget to leave a review for your " + serviceName + " appointment with " + stylistName + ".";
        }

        Notification notification = new Notification(message);
        notificationList.add(notification);

        notificationAdapter.setNotifications(notificationList);

        if (timeContext.equals("24 hours")) {
            Intent intent = new Intent(this, ReviewActivity.class);
            intent.putExtra("service_name", serviceName);
            intent.putExtra("stylist_name", stylistName);
            intent.putExtra("timeContext", "reviewReminder");
            startActivity(intent);
        }

        Toast.makeText(this, "New Notification: " + message, Toast.LENGTH_LONG).show();
    }


    public void openUserMainPage(View view) {
        Intent intent = new Intent(AppointmentNotificationActivity.this, UserMainPageActivity.class);
        startActivity(intent);
    }

    public void openServicesList(View view) {
        Intent intent = new Intent(AppointmentNotificationActivity.this, ServiceList.class);
        startActivity(intent);
    }

    public void openStylistList(View view) {
        Intent intent = new Intent(AppointmentNotificationActivity.this, ViewStylistList.class);
        startActivity(intent);
    }

    public void openReservationPage(View view) {
        Intent intent = new Intent(AppointmentNotificationActivity.this, AppointmentReservationActivity.class);
        startActivity(intent);
    }

    public void openNotificationPage(View view) {
        Intent intent = new Intent(AppointmentNotificationActivity.this, AppointmentNotificationActivity.class);
        startActivity(intent);
    }

    public void openProfilePage(View view) {
        Intent intent = new Intent(AppointmentNotificationActivity.this, ProfileActivity.class);
        startActivity(intent);
    }
}
