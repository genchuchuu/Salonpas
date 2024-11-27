package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AdminNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_notification);


        String notificationMessage1 = "Someone just reserved an Appointment";
        String notificationTime1 = "Scheduled for: 3:00 PM";

        String notificationMessage2 = "Someone just finished their Appointment";
        String notificationTime2 = "Finished at: 3:30 PM";

        TextView notificationMessageView1 = findViewById(R.id.notificationMessage1);
        TextView notificationTimeView1 = findViewById(R.id.notificationTime1);

        TextView notificationMessageView2 = findViewById(R.id.notificationMessage2);
        TextView notificationTimeView2 = findViewById(R.id.notificationTime2);

        notificationMessageView1.setText(notificationMessage1);
        notificationTimeView1.setText(notificationTime1);

        notificationMessageView2.setText(notificationMessage2);
        notificationTimeView2.setText(notificationTime2);
    }
    public void openAdminMainPage(View view) {
        Intent intent = new Intent(AdminNotificationActivity.this, AdminMainPageActivity.class);
        startActivity(intent);
    }

    public void manageService(View view) {
        Intent intent = new Intent(AdminNotificationActivity.this, ManageServiceList.class);
        startActivity(intent);
    }

    public void manageStylist(View view) {
        Intent intent = new Intent(AdminNotificationActivity.this, ManageStylist.class);
        startActivity(intent);
    }

    public void manageAppointment(View view) {
        Intent intent = new Intent(AdminNotificationActivity.this, ManageAppointment.class);
        startActivity(intent);
    }

    public void openAdminNotification(View view) {
        Intent intent = new Intent(AdminNotificationActivity.this, AdminNotificationActivity.class);
        startActivity(intent);
    }

    public void openAdminProfile(View view) {
        Intent intent = new Intent(AdminNotificationActivity.this, AdminProfile.class);
        startActivity(intent);
    }
}
