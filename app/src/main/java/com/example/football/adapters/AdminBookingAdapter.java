package com.example.football.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.models.Booking;

import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.ViewHolder> {

    private final List<Booking> bookingList;
    private final OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onRowActionClick(Booking booking);
    }

    public AdminBookingAdapter(List<Booking> bookingList, OnBookingClickListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Gán chuẩn xác file item_booking_admin như Khánh đã đổi tên
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.txtRowStt.setText(String.valueOf(position + 1));
        holder.txtBookingCourtName.setText(booking.getCourtName());
        holder.txtBookingTime.setText("📅 " + booking.getDate() + " | ⏰ " + booking.getSlotTime());
        holder.txtBookingPhone.setText("📞 SĐT: " + booking.getUserPhone());

        // 👉 ĐÃ FIX TRIỆT ĐỂ: Ép ẩn tạm thời dòng text dịch vụ để dọn sạch lỗi sập build method getServices()
        holder.txtAdminBookingServices.setVisibility(View.GONE);

        holder.txtBookingPrice.setText(String.format("%,d đ", booking.getTotalPrice()));

        String status = booking.getStatus();
        holder.txtBookingStatus.setText(status);

        if ("Đã xác nhận".equals(status)) {
            holder.txtBookingStatus.setTextColor(Color.parseColor("#22C55E"));
            holder.btnRowAction.setImageResource(android.R.drawable.checkbox_on_background);
            holder.btnRowAction.setColorFilter(Color.parseColor("#22C55E"));
        } else if ("Đã hủy".equals(status)) {
            holder.txtBookingStatus.setTextColor(Color.parseColor("#EF4444"));
            holder.btnRowAction.setImageResource(android.R.drawable.ic_delete);
            holder.btnRowAction.setColorFilter(Color.parseColor("#EF4444"));
        } else {
            holder.txtBookingStatus.setTextColor(Color.parseColor("#F59E0B"));
            holder.btnRowAction.setImageResource(android.R.drawable.ic_menu_rotate);
            holder.btnRowAction.setColorFilter(Color.parseColor("#F59E0B"));
        }

        holder.btnRowAction.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRowActionClick(booking);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRowStt, txtBookingCourtName, txtBookingTime, txtBookingPhone, txtAdminBookingServices, txtBookingPrice, txtBookingStatus;
        ImageView btnRowAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRowStt = itemView.findViewById(R.id.txtRowStt);
            txtBookingCourtName = itemView.findViewById(R.id.txtBookingCourtName);
            txtBookingTime = itemView.findViewById(R.id.txtBookingTime);
            txtBookingPhone = itemView.findViewById(R.id.txtBookingPhone);
            txtAdminBookingServices = itemView.findViewById(R.id.txtAdminBookingServices);
            txtBookingPrice = itemView.findViewById(R.id.txtBookingPrice);
            txtBookingStatus = itemView.findViewById(R.id.txtBookingStatus);
            btnRowAction = itemView.findViewById(R.id.btnRowAction);
        }
    }
}