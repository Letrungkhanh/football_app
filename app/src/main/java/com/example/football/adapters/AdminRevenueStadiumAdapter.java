package com.example.football.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.models.Stadium;

import java.util.List;

public class AdminRevenueStadiumAdapter extends RecyclerView.Adapter<AdminRevenueStadiumAdapter.ViewHolder> {

    private final List<Stadium> stadiumList;
    private final OnStadiumClickListener listener;

    public interface OnStadiumClickListener {
        void onStadiumClick(Stadium stadium);
    }

    public AdminRevenueStadiumAdapter(List<Stadium> stadiumList, OnStadiumClickListener listener) {
        this.stadiumList = stadiumList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 👉 SỬA THẦN TỐC: Nạp layout 2 dòng mặc định của hệ thống Android, không sợ lệch file XML của dự án
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stadium stadium = stadiumList.get(position);

        // Gán tên cụm sân to lên dòng số 1
        if (holder.text1 != null && stadium.getName() != null) {
            holder.text1.setText(stadium.getName());
            holder.text1.setTextSize(16);
            holder.text1.setPadding(16, 8, 16, 2);
            holder.text1.setTypeface(null, android.graphics.Typeface.BOLD);
        }

        // Gán số tiền doanh thu lên dòng số 2
        if (holder.text2 != null && stadium.getAddress() != null) {
            holder.text2.setText(stadium.getAddress()); // Hiện "Doanh thu cụm: ... VND"
            holder.text2.setTextSize(14);
            holder.text2.setPadding(16, 2, 16, 8);
            holder.text2.setTextColor(android.graphics.Color.parseColor("#015938")); // Màu xanh Alobo rực rỡ
        }

        // Bắt sự kiện click vào dòng để mở chi tiết phân tầng
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStadiumClick(stadium);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stadiumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text1, text2;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Sử dụng ID hệ thống mặc định của simple_list_item_2, hết sạch lỗi Cannot resolve
            text1 = itemView.findViewById(android.R.id.text1);
            text2 = itemView.findViewById(android.R.id.text2);
        }
    }
}