package com.mobdeve.salonpas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class StylistAdapter extends RecyclerView.Adapter<StylistAdapter.StylistViewHolder> {

    private List<Stylist> stylistList;
    private OnStylistClickListener listener;

    public StylistAdapter(List<Stylist> stylistList, OnStylistClickListener listener) {
        this.stylistList = stylistList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.stylist_item, parent, false);
        return new StylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StylistViewHolder holder, int position) {
        Stylist stylist = stylistList.get(position);
        holder.stylistName.setText(stylist.getName());
        holder.stylistServices.setText(stylist.getServices());
        Glide.with(holder.itemView.getContext());

        holder.itemView.setOnClickListener(v -> listener.onStylistClick(stylist));
    }

    @Override
    public int getItemCount() {
        return stylistList.size();
    }

    static class StylistViewHolder extends RecyclerView.ViewHolder {
        TextView stylistName, stylistServices;


        public StylistViewHolder(@NonNull View itemView) {
            super(itemView);
            stylistName = itemView.findViewById(R.id.stylistName);
            stylistServices = itemView.findViewById(R.id.stylistServices);

        }
    }

    public interface OnStylistClickListener {
        void onStylistClick(Stylist stylist);
    }
}
