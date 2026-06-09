package com.example.football.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.football.R;
import java.util.List;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.SlotViewHolder> {

    private final List<String> totalSlots;       // Tất cả các ca
    private final List<String> bookedSlots;      // Các ca đã có người đặt (Quét từ Firebase)
    private int selectedPosition = -1;           // Vị trí ô User đang bấm chọn
    private final OnSlotClickListener listener;

    public interface OnSlotClickListener {
        void onSlotClick(String slotTime);
    }

    public TimeSlotAdapter(List<String> totalSlots, List<String> bookedSlots, OnSlotClickListener listener) {
        this.totalSlots = totalSlots;
        this.bookedSlots = bookedSlots;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_time_slot, parent, false);
        return new SlotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotViewHolder holder, int position) {
        String slotTime = totalSlots.get(position);
        holder.txtTime.setText(slotTime);

        if (bookedSlots.contains(slotTime)) {
            // =========================================================================
            // 🔴 TRƯỜNG HỢP 1: Ca này đã có người đặt rồi -> Hóa đỏ và KHÓA CỨNG
            // =========================================================================
            holder.cardSlot.setCardBackgroundColor(Color.parseColor("#EF4444")); // Nền màu đỏ rực
            holder.txtTime.setTextColor(Color.WHITE);
            //Toast.makeText(this,"khung giờ đã được đặt",Toast.LENGTH_SHORT).show();

            // 👉 SỬA CHÍ MẠNG: Xóa bỏ hoàn toàn bộ lắng nghe click, đố ai bấm được luôn
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(false);
            holder.itemView.setFocusable(false);

        } else {
            // Ca này còn trống -> Mở quyền cài đặt click
            holder.itemView.setClickable(true);
            holder.itemView.setFocusable(true);

            if (position == selectedPosition) {
                // 🟢 TRƯỜNG HỢP 2: Ca trống và đang được User click chọn
                holder.cardSlot.setCardBackgroundColor(Color.parseColor("#10B981")); // Màu xanh Emerald
                holder.txtTime.setTextColor(Color.WHITE);
            } else {
                // ⚪ TRƯỜNG HỢP 3: Ca trống hoàn toàn chưa có ai chọn
                holder.cardSlot.setCardBackgroundColor(Color.WHITE); // Màu trắng sạch sẽ
                holder.txtTime.setTextColor(Color.parseColor("#1E293B"));
            }

            // 👉 CHỈ GÁN SỰ KIỆN CLICK CHO CA TRỐNG HỢP LỆ
            holder.itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = holder.getAdapterPosition();

                // Vẽ lại giao diện ô cũ và ô mới chọn để cập nhật màu sắc mượt mà
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);

                // Đẩy dữ liệu thời gian về cho BookingActivity
                listener.onSlotClick(slotTime);
            });
        }
    }

    @Override
    public int getItemCount() {
        return totalSlots.size();
    }

    public static class SlotViewHolder extends RecyclerView.ViewHolder {
        CardView cardSlot;
        TextView txtTime;

        public SlotViewHolder(@NonNull View itemView) {
            super(itemView);
            cardSlot = itemView.findViewById(R.id.cardTimeSlot);
            txtTime = itemView.findViewById(R.id.txtTimeSlotValue);
        }
    }
}