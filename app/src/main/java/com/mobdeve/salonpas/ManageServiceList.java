package com.mobdeve.salonpas;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ManageServiceList extends AppCompatActivity implements ServiceAdapter.OnServiceClickListener {

    private RecyclerView serviceRecyclerView;
    private ServiceAdapter serviceAdapter;
    private List<Service> serviceList;
    private List<Service> allServices;
    private Button addServiceButton;
    private DatabaseReference servicesRef;
    private EditText searchBar;
    private ImageView searchIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_service_list);

        serviceRecyclerView = findViewById(R.id.ServiceListRecView);
        addServiceButton = findViewById(R.id.addServiceButton);
        searchBar = findViewById(R.id.searchBar);
        searchIcon = findViewById(R.id.searchIcon);

        serviceList = new ArrayList<>();
        allServices = new ArrayList<>();
        servicesRef = FirebaseDatabase.getInstance().getReference("Services");

        serviceAdapter = new ServiceAdapter(serviceList, this);
        serviceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        serviceRecyclerView.setAdapter(serviceAdapter);

        loadServicesFromFirebase();

        addServiceButton.setOnClickListener(v -> showAddServiceDialog());

        setupSearch();
    }

    private void loadServicesFromFirebase() {
        servicesRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceList.clear();
                allServices.clear();

                for (DataSnapshot serviceSnapshot : dataSnapshot.getChildren()) {
                    Service service = serviceSnapshot.getValue(Service.class);
                    if (service != null) {
                        service.setId(serviceSnapshot.getKey());
                        serviceList.add(service);
                        allServices.add(service);
                    }
                }
                serviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManageServiceList.this, "Failed to load services: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onServiceClick(Service service) {
        showEditServiceDialog(service);
    }

    private void showEditServiceDialog(Service service) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Service");

        View view = getLayoutInflater().inflate(R.layout.dialogue_edit_service, null);
        builder.setView(view);

        TextInputEditText nameInput = view.findViewById(R.id.EditSrvName);
        TextInputEditText descInput = view.findViewById(R.id.EditSrvDesc);
        TextInputEditText durationInput = view.findViewById(R.id.EditSrvDuration);
        TextInputEditText priceInput = view.findViewById(R.id.EditSrvPrice);
        ImageView serviceImage = view.findViewById(R.id.EditSrvImg);

        nameInput.setText(service.getName());
        descInput.setText(service.getDescription());
        durationInput.setText(service.getDuration());
        priceInput.setText(service.getPrice());

        Glide.with(this)
                .load(service.getImageUrl())
                .placeholder(getDefaultServiceImage(service.getName()))
                .into(serviceImage);


        builder.setPositiveButton("Save", (dialog, which) -> {
            service.setName(nameInput.getText().toString());
            service.setDescription(descInput.getText().toString());
            service.setDuration(durationInput.getText().toString());
            service.setPrice(priceInput.getText().toString());

            if (service.getId() != null) {
                updateServiceInFirebase(service);
            } else {
                Toast.makeText(this, "Service ID is missing. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Delete", (dialog, which) -> showDeleteConfirmationDialog(service));

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private int getDefaultServiceImage(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "hair treatment":
                return R.drawable.hairtreatment;
            case "hairstyle":
                return R.drawable.hairstyle;
            case "haircut":
                return R.drawable.haircut;
            case "hair color":
                return R.drawable.haircolor;
            case "rebond":
                return R.drawable.rebond;
            default:
                return R.drawable.placeholder; // Fallback image
        }
    }


    private void updateServiceInFirebase(Service service) {
        String serviceId = service.getId();

        if (serviceId != null && !serviceId.isEmpty()) {
            servicesRef.child(serviceId).setValue(service)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Service updated successfully.", Toast.LENGTH_SHORT).show();
                        loadServicesFromFirebase(); // Reload services to reflect changes
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to update service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Service ID is invalid. Update failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog(Service service) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Service")
                .setMessage("Are you sure you want to delete this service?")
                .setPositiveButton("Yes", (dialog, which) -> deleteServiceFromFirebase(service))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteServiceFromFirebase(Service service) {
        String serviceId = service.getId();
        servicesRef.child(serviceId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Service deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showAddServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Service");

        View view = getLayoutInflater().inflate(R.layout.dialogue_add_service, null);
        builder.setView(view);

        TextInputEditText nameInput = view.findViewById(R.id.AddSrvName);
        TextInputEditText descInput = view.findViewById(R.id.AddSrvDesc);
        TextInputEditText durationInput = view.findViewById(R.id.AddSrvDuration);
        TextInputEditText priceInput = view.findViewById(R.id.AddSrvPrice);

        builder.setPositiveButton("Add", (dialog, which) -> {
            Service newService = new Service(
                    nameInput.getText().toString(),
                    descInput.getText().toString(),
                    durationInput.getText().toString(),
                    priceInput.getText().toString(),
                    null
            );
            addServiceToFirebase(newService);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

    private void addServiceToFirebase(Service service) {
        String serviceId = servicesRef.push().getKey();
        if (serviceId != null) {
            service.setId(serviceId);
            servicesRef.child(serviceId).setValue(service)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Service added successfully.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to add service: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void setupSearch() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence query, int start, int before, int count) {
                filterServices(query.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        searchIcon.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim();
            filterServices(query);
        });
    }

    private void filterServices(String query) {
        serviceList.clear();
        if (query.isEmpty()) {
            serviceList.addAll(allServices);
        } else {
            for (Service service : allServices) {
                if (service.getName().toLowerCase().contains(query.toLowerCase())) {
                    serviceList.add(service);
                }
            }
        }
        serviceAdapter.notifyDataSetChanged();
    }
}
