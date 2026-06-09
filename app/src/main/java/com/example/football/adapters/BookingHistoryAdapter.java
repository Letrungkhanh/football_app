package com.example.football.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.models.Booking;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.HistoryViewHolder> {

    private final List<Booking> bookingList;

    public BookingHistoryAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp đúng file layout lịch sử đồ uống của bạn
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        holder.txtCourtName.setText(booking.getCourtName());
        holder.txtDateTime.setText(String.format("📅 Thời gian: %s | %s", booking.getSlotTime(), booking.getDate()));
        holder.txtPrice.setText(String.format("💰 Tổng tiền: %,d đ", booking.getTotalPrice()));
        holder.txtStatus.setText(booking.getStatus());

        // =========================================================================
        // 👉 ĐÃ BỔ SUNG: Gán chuỗi danh sách nước uống đã đặt từ Firebase lên màn hình User
        // =========================================================================
        if (booking.getSelectedServices() != null && !booking.getSelectedServices().isEmpty()) {
            holder.txtServices.setText("🥤 Dịch vụ: " + booking.getSelectedServices());
        } else {
            holder.txtServices.setText("🥤 Dịch vụ: Không chọn dịch vụ");
        }
        // =========================================================================

        // Đổi màu chữ trạng thái giữ nguyên của bạn
        if ("Chờ duyệt".equals(booking.getStatus())) {
            holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#F59E0B"));
        } else if ("Đã hoàn thành".equals(booking.getStatus()) || "Đã duyệt".equals(booking.getStatus()) || "Đã xác nhận".equals(booking.getStatus())) {
            holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#10B981"));
        } else {
            holder.txtStatus.setTextColor(android.graphics.Color.parseColor("#EF4444"));
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        // 👉 ĐÃ THÊM: Biến txtServices ở đây
        TextView txtCourtName, txtDateTime, txtPrice, txtStatus, txtServices;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCourtName = itemView.findViewById(R.id.txtHistoryCourtName);
            txtDateTime = itemView.findViewById(R.id.txtHistoryDateTime);
            txtPrice = itemView.findViewById(R.id.txtHistoryPrice);
            txtStatus = itemView.findViewById(R.id.txtHistoryStatus);

            // 👉 ĐÃ THÊM: Ánh xạ chuẩn ID từ file XML item_booking_history của bạn
            txtServices = itemView.findViewById(R.id.txtUserBookingServices);
        }
    }
}