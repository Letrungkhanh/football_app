package com.example.football.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.models.Booking;

import java.text.DecimalFormat;
import java.util.List;

public class AdminRevenueBookingAdapter extends RecyclerView.Adapter<AdminRevenueBookingAdapter.ViewHolder> {

    private final List<Booking> bookingList;
    private final DecimalFormat formatter = new DecimalFormat("#,### VND");

    public AdminRevenueBookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_revenue_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // 1. Gán số điện thoại khách đặt sân bóng
        if (booking.getUserPhone() != null && !booking.getUserPhone().isEmpty()) {
            holder.txtBookingPhone.setText("Khách: " + booking.getUserPhone());
        } else {
            holder.txtBookingPhone.setText("Khách hàng Alobo");
        }

        // 2. Gán số tiền tổng hóa đơn (Ép kiểu từ int sang chuỗi tiền tệ phẳng)
        holder.txtBookingPrice.setText("+" + formatter.format(booking.getTotalPrice()));

        // 3. 👉 ĐÃ ĐỒNG BỘ: Kết hợp ngày (date) và khung giờ (slotTime) đúng chuẩn model của Khánh
        String bookingDate = booking.getDate();
        String bookingSlot = booking.getSlotTime();

        if (bookingDate != null && bookingSlot != null) {
            holder.txtBookingTime.setText(bookingDate + " (" + bookingSlot + ")");
        } else if (bookingDate != null) {
            holder.txtBookingTime.setText(bookingDate);
        } else {
            holder.txtBookingTime.setText("Lịch sử hệ thống");
        }
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtBookingPhone, txtBookingTime, txtBookingPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBookingPhone = itemView.findViewById(R.id.txtBookingPhone);
            txtBookingTime = itemView.findViewById(R.id.txtBookingTime);
            txtBookingPrice = itemView.findViewById(R.id.txtBookingPrice);
        }
    }
}