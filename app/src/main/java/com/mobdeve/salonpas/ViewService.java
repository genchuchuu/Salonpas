package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewService extends AppCompatActivity {

    private TextView serviceNameTextView, serviceDescTextView, serviceDurationTextView, servicePriceTextView;
    private ImageView serviceImg;
    private RecyclerView stylistRecyclerView;
    private StylistAdapter stylistAdapter;
    private List<Stylist> stylistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_service);

        serviceNameTextView = findViewById(R.id.serviceName);
        serviceDescTextView = findViewById(R.id.serviceDesc);
        serviceDurationTextView = findViewById(R.id.serviceDuration);
        servicePriceTextView = findViewById(R.id.servicePrice);
        serviceImg = findViewById(R.id.serviceImg);
        stylistRecyclerView = findViewById(R.id.StylistRecView);

        stylistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        stylistList = new ArrayList<>();
        stylistAdapter = new StylistAdapter(stylistList, stylist -> {
            Toast.makeText(ViewService.this, "Selected: " + stylist.getName(), Toast.LENGTH_SHORT).show();
        });
        stylistRecyclerView.setAdapter(stylistAdapter);

        String srvName = getIntent().getStringExtra("name");
        String srvDesc = getIntent().getStringExtra("desc");
        String srvDuration = getIntent().getStringExtra("duration");
        String srvPrice = getIntent().getStringExtra("price");
        String srvImageUrl = getIntent().getStringExtra("imageUrl");

        serviceNameTextView.setText(srvName);
        serviceDescTextView.setText(srvDesc);
        serviceDurationTextView.setText(srvDuration);
        servicePriceTextView.setText(srvPrice);

        if (srvImageUrl != null && !srvImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(srvImageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(serviceImg);
        } else {
            switch (srvName.toLowerCase()) {
                case "hair treatment":
                    serviceImg.setImageResource(R.drawable.hairtreatment);
                    break;
                case "hair styling":
                    serviceImg.setImageResource(R.drawable.hairstyle);
                    break;
                case "haircut":
                    serviceImg.setImageResource(R.drawable.haircut);
                    break;
                case "hair color":
                    serviceImg.setImageResource(R.drawable.haircolor);
                    break;
                case "rebond":
                    serviceImg.setImageResource(R.drawable.rebond);
                    break;
                default:
                    serviceImg.setImageResource(R.drawable.placeholder);
                    break;
            }
        }


        loadStylistsForService(srvName);
    }

    private void loadStylistsForService(String serviceName) {
        DatabaseReference stylistsRef = FirebaseDatabase.getInstance().getReference("Stylists");
        stylistsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stylistList.clear();
                for (DataSnapshot stylistSnapshot : snapshot.getChildren()) {
                    Stylist stylist = stylistSnapshot.getValue(Stylist.class);
                    if (stylist != null && stylist.getServices() != null) {
                        if (stylist.getServices().contains(serviceName)) {
                            stylistList.add(stylist);
                        }
                    }
                }
                stylistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewService.this, "Failed to load stylists: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void backServicePage(View view) {
        startActivity(new Intent(ViewService.this, ServiceList.class));
    }
}
