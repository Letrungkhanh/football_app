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

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.ViewHolder> {

    private final List<Booking> bookingList;
    private final OnBookingClickListener listener;

    // 👉 ĐÃ ĐỒNG BỘ INTERFACE: Chuyển 2 nút Duyệt/Hủy cũ thành 1 hàm hành động nhanh duy nhất
    public interface OnBookingClickListener {
        void onRowActionClick(Booking booking);
    }

    public BookingAdapter(List<Booking> bookingList, OnBookingClickListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp chính xác file layout item phẳng mới dán của Khánh
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookingList.get(position);

        // 1. Gán số thứ tự cột STT tự động tăng
        holder.txtRowStt.setText(String.valueOf(position + 1));

        // 2. Đổ thông tin tên sân
        holder.txtBookingCourtName.setText(booking.getCourtName());

        // 3. Đổ ngày giờ đặt sân
        holder.txtBookingTime.setText("📅 " + booking.getDate() + " | ⏰ " + booking.getSlotTime());

        // 4. Đổ số điện thoại khách đặt
        holder.txtBookingPhone.setText("📞 SĐT: " + booking.getUserPhone());

        // 👉 ĐÃ FIX TRIỆT ĐỂ: Ép ẩn tạm thời dòng text dịch vụ nước uống để dọn sạch lỗi getServices()
        holder.txtAdminBookingServices.setVisibility(View.GONE);

        // 5. Đổ tổng số tiền thuê sân bóng
        holder.txtBookingPrice.setText(String.format("%,d đ", booking.getTotalPrice()));

        // 6. Cấu hình trạng thái màu chữ và icon hành động xoay đảo tương ứng
        String status = booking.getStatus();
        holder.txtBookingStatus.setText(status);

        if ("Đã xác nhận".equalsIgnoreCase(status)) {
            holder.txtBookingStatus.setTextColor(Color.parseColor("#22C55E")); // Xanh lá
            holder.btnRowAction.setImageResource(android.R.drawable.checkbox_on_background);
            holder.btnRowAction.setColorFilter(Color.parseColor("#22C55E"));
        } else if ("Đã hủy".equalsIgnoreCase(status)) {
            holder.txtBookingStatus.setTextColor(Color.parseColor("#EF4444")); // Đỏ
            holder.btnRowAction.setImageResource(android.R.drawable.ic_delete);
            holder.btnRowAction.setColorFilter(Color.parseColor("#EF4444"));
        } else {
            holder.txtBookingStatus.setTextColor(Color.parseColor("#F59E0B")); // Vàng (Chờ duyệt)
            holder.btnRowAction.setImageResource(android.R.drawable.ic_menu_rotate);
            holder.btnRowAction.setColorFilter(Color.parseColor("#F59E0B"));
        }

        // 👉 Bắt sự kiện click nút hành động đảo trạng thái nhanh ở rìa phải giống trang AdminBookingAdapter
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
        // 👉 ĐÃ CẬP NHẬT: Khai báo đúng các biến theo cấu trúc bảng phẳng đồng bộ
        TextView txtRowStt, txtBookingCourtName, txtBookingTime, txtBookingPhone, txtAdminBookingServices, txtBookingPrice, txtBookingStatus;
        ImageView btnRowAction;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ khớp 100% với các ID phẳng trong file XML item_booking_admin.xml
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