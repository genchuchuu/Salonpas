package com.mobdeve.salonpas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {
    private final List<Service> serviceList;
    private final OnServiceClickListener serviceClickListener;

    public interface OnServiceClickListener {
        void onServiceClick(Service service);
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceClickListener listener) {
        this.serviceList = serviceList;
        this.serviceClickListener = listener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_items, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.bind(service);
        holder.itemView.setOnClickListener(v -> serviceClickListener.onServiceClick(service));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {
        private final TextView serviceName, serviceDescription, serviceDuration, servicePrice;
        private final ImageView serviceImage;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.serviceName);
            serviceDescription = itemView.findViewById(R.id.serviceDesc);
            serviceDuration = itemView.findViewById(R.id.serviceDuration);
            servicePrice = itemView.findViewById(R.id.servicePrice);
            serviceImage = itemView.findViewById(R.id.serviceImg);
        }

        public void bind(Service service) {
            serviceName.setText(service.getName());
            serviceDescription.setText(service.getDescription());
            serviceDuration.setText(service.getDuration());
            servicePrice.setText(service.getPrice());

            // Check if the image URL is available
            if (service.getImageUrl() != null && !service.getImageUrl().isEmpty()) {
                Glide.with(serviceImage.getContext())
                        .load(service.getImageUrl())
                        .placeholder(R.drawable.placeholder) // Fallback placeholder during loading
                        .into(serviceImage);
            } else {
                // Assign default images based on service type
                switch (service.getName().toLowerCase()) {
                    case "hair treatment":
                        serviceImage.setImageResource(R.drawable.hairtreatment);
                        break;
                    case "haircut":
                        serviceImage.setImageResource(R.drawable.haircut);
                        break;
                    case "hair color":
                        serviceImage.setImageResource(R.drawable.haircolor);
                        break;
                    case "hair styling":
                        serviceImage.setImageResource(R.drawable.hairstyle);
                        break;
                    case "rebond":
                        serviceImage.setImageResource(R.drawable.rebond);
                        break;
                    default:
                        serviceImage.setImageResource(R.drawable.placeholder); // General fallback
                        break;
                }
            }
        }

    }
}
