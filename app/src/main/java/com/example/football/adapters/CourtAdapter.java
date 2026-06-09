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
import com.example.football.models.Court;

import java.util.List;

public class CourtAdapter extends RecyclerView.Adapter<CourtAdapter.ViewHolder> {

    private List<Court> courtList;
    private OnCourtClickListener clickListener;

    public interface OnCourtClickListener {
        void onEditClick(Court court);
        void onDeleteClick(String courtId);
        void onCourtClick(Court court); // Giữ lại để khớp với hàm implements bên ManageCourtActivity
    }

    public CourtAdapter(List<Court> courtList, OnCourtClickListener clickListener) {
        this.courtList = courtList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Khởi tạo layout item phẳng mới dán của Khánh
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_court_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Court court = courtList.get(position);

        // 1. Gán số thứ tự cột STT tăng tự động (bắt đầu từ 1)
        holder.txtRowStt.setText(String.valueOf(position + 1));

        // 2. Đổ tên sân lên cột thứ 2 (Ví dụ: Sân số 1)
        holder.txtRowMainTitle.setText(court.getName());

        // 3. Đổ loại sân lên cột thứ 3 (Ví dụ: 5 người, 7 người)
        holder.txtRowSubValue.setText(court.getType());

        // 👉 MẸO ĐỒNG BỘ: Đổi màu huy hiệu dựa theo loại sân cho chuyên nghiệp
        if ("5 người".equals(court.getType())) {
            holder.imgRowBadge.setColorFilter(Color.parseColor("#4ADE80")); // Màu xanh lá sinh động
        } else if ("7 người".equals(court.getType())) {
            holder.imgRowBadge.setColorFilter(Color.parseColor("#3B82F6")); // Màu xanh dương xịn sò
        } else {
            holder.imgRowBadge.setColorFilter(Color.parseColor("#FACC15")); // Màu vàng cát nổi bật
        }

        // 👉 BẤM VÀO THÂN DÒNG (Toàn bộ ô) sẽ mở luồng SỬA (Edit) thông tin sân
        holder.itemView.setOnClickListener(v -> clickListener.onEditClick(court));

        // 👉 BẤM VÀO NÚT THÙNG RÁC MÀU ĐỎ CAM để kích hoạt XÓA sân
        holder.btnRowDelete.setOnClickListener(v -> {
            if (court.getId() != null) {
                clickListener.onDeleteClick(court.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return courtList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 👉 ĐÃ CẬP NHẬT: Khai báo đúng các View phẳng theo form mẫu đồng bộ
        TextView txtRowStt, txtRowMainTitle, txtRowSubValue;
        ImageView imgRowBadge, btnRowDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ khớp 100% với các ID mới dán trong item_court_admin.xml
            txtRowStt = itemView.findViewById(R.id.txtRowStt);
            txtRowMainTitle = itemView.findViewById(R.id.txtRowMainTitle);
            txtRowSubValue = itemView.findViewById(R.id.txtRowSubValue);
            imgRowBadge = itemView.findViewById(R.id.imgRowBadge);
            btnRowDelete = itemView.findViewById(R.id.btnRowDelete);
        }
    }
}