package com.example.football.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminRevenueCourtAdapter extends RecyclerView.Adapter<AdminRevenueCourtAdapter.ViewHolder> {

    private final List<String> courtIds;
    private final Map<String, Double> courtRevenueMap;
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");

    public AdminRevenueCourtAdapter(Map<String, Double> courtRevenueMap) {
        this.courtRevenueMap = courtRevenueMap;
        // Chuyển các Key (mã sân con) trong Map thành một danh sách List để RecyclerView chạy chỉ mục index
        this.courtIds = new ArrayList<>(courtRevenueMap.keySet());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revenue_court, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String courtId = courtIds.get(position);
        double revenue = courtRevenueMap.get(courtId) != null ? courtRevenueMap.get(courtId) : 0;

        // Nếu Firebase lưu mã ID dạng chuỗi (Ví dụ: court1), hệ thống sẽ gán text hiển thị
        holder.txtCourtName.setText("Mã sân: " + courtId);
        holder.txtCourtPrice.setText(formatter.format(revenue));
    }

    @Override
    public int getItemCount() {
        return courtIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtCourtName, txtCourtPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCourtName = itemView.findViewById(R.id.txtCourtName);
            txtCourtPrice = itemView.findViewById(R.id.txtCourtPrice);
        }
    }
}