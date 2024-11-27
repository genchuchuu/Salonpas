package com.mobdeve.salonpas;

import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private List<Appointment> appointmentList;
    private List<String> appointmentIdList;
    private OnReviewClickListener onReviewClickListener;

    public interface OnReviewClickListener {
        void onReviewClick(String appointmentId, String serviceName, String stylistName);
    }

    public AppointmentAdapter(List<Appointment> appointmentList, List<String> appointmentIdList, OnReviewClickListener onReviewClickListener) {
        this.appointmentList = appointmentList;
        this.appointmentIdList = appointmentIdList;
        this.onReviewClickListener = onReviewClickListener;
    }


    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_item, parent, false);
        return new AppointmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = appointmentList.get(position);
        String appointmentId = appointmentIdList.get(position);

        holder.appointmentDate.setText(appointment.getDateTime());
        holder.serviceDescription.setText(appointment.getServiceName());
        holder.stylistName.setText(appointment.getStylistName());

        // Review Button logic (only for user workflows)
        if (onReviewClickListener != null && !isUpcoming(appointment.getDateTime())) {
            holder.reviewButton.setVisibility(View.VISIBLE);
            holder.reviewButton.setOnClickListener(v -> {
                onReviewClickListener.onReviewClick(appointmentId, appointment.getServiceName(), appointment.getStylistName());
            });

        } else {
            holder.reviewButton.setVisibility(View.GONE);
        }
    }

    private boolean isUpcoming(String appointmentDateTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate date = LocalDate.parse(appointmentDateTime.split(" at ")[0]); // Extract date
            return date.isAfter(LocalDate.now());
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return appointmentList.size();
    }

    static class AppointmentViewHolder extends RecyclerView.ViewHolder {
        TextView appointmentDate;
        TextView serviceDescription;
        TextView stylistName;
        Button reviewButton;

        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            appointmentDate = itemView.findViewById(R.id.appointmentDate);
            serviceDescription = itemView.findViewById(R.id.serviceDescription);
            stylistName = itemView.findViewById(R.id.stylistName);
            reviewButton = itemView.findViewById(R.id.reviewButton); // Ensure this exists in XML
        }
    }
}
