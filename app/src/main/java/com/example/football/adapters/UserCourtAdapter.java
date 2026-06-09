package com.example.football.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.models.Court;

import java.util.List;

public class UserCourtAdapter extends RecyclerView.Adapter<UserCourtAdapter.ViewHolder> {

    private List<Court> courtList;
    private OnCourtClickListener listener;

    public interface OnCourtClickListener {
        void onBookClick(Court court);
    }

    public UserCourtAdapter(List<Court> courtList, OnCourtClickListener listener) {
        this.courtList = courtList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_court_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Court court = courtList.get(position);

        holder.txtCourtNameUser.setText(court.getName());
        holder.txtCourtTypeUser.setText("Loại sân: " + court.getType());
        holder.txtCourtPriceUser.setText(String.format("%,d đ", court.getPrice()));

        holder.btnBookCourtNow.setOnClickListener(v -> listener.onBookClick(court));
    }

    @Override
    public int getItemCount() {
        return courtList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCourtNameUser, txtCourtTypeUser, txtCourtPriceUser;
        Button btnBookCourtNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCourtNameUser = itemView.findViewById(R.id.txtCourtNameUser);
            txtCourtTypeUser = itemView.findViewById(R.id.txtCourtTypeUser);
            txtCourtPriceUser = itemView.findViewById(R.id.txtCourtPriceUser);
            btnBookCourtNow = itemView.findViewById(R.id.btnBookCourtNow);
        }
    }
}