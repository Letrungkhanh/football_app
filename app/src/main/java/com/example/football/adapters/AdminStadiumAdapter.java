package com.example.football.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.models.Stadium;

import java.util.List;

public class AdminStadiumAdapter extends RecyclerView.Adapter<AdminStadiumAdapter.ViewHolder> {

    private final List<Stadium> stadiumList;
    private final OnStadiumClickListener clickListener;

    // Interface giữ nguyên 100% để khớp với ManageStadiumActivity của Khánh
    public interface OnStadiumClickListener {
        void onItemClick(Stadium stadium);
        void onEditClick(Stadium stadium);
        void onDeleteClick(String stadiumId);
    }

    public AdminStadiumAdapter(List<Stadium> stadiumList, OnStadiumClickListener clickListener) {
        this.stadiumList = stadiumList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Nạp đúng file layout item phẳng mới dán của Khánh
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stadium_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stadium stadium = stadiumList.get(position);

        // 1. Gán số thứ tự cột STT tự động tăng (bắt đầu từ 1)
        holder.txtRowStt.setText(String.valueOf(position + 1));

        // 2. Đổ tên cụm sân lớn lên cột thứ 2 (Ví dụ: Đại học Vinh)
        holder.txtRowMainTitle.setText(stadium.getName());

        // 3. Đổ địa chỉ cụm sân lên cột thứ 3 kèm icon vị trí định vị
        holder.txtRowSubValue.setText(stadium.getAddress());

        // 👉 LUỒNG TRẢI NGHIỆM ĐỒNG BỘ:
        // Bấm vào thân dòng (Toàn bộ ô) sẽ mở danh sách sân nhỏ (ManageCourtActivity)
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(stadium));

        // 👉 BẤM GIỮ LÂU (Long Click) vào dòng sẽ mở luồng SỬA (Edit) thông tin cụm sân
        holder.itemView.setOnLongClickListener(v -> {
            clickListener.onEditClick(stadium);
            return true;
        });

        // 👉 BẤM VÀO NÚT THÙNG RÁC MÀU ĐỎ CAM ở bên phải để XÓA cụm sân lớn
        holder.btnRowDelete.setOnClickListener(v -> {
            if (stadium.getId() != null) {
                clickListener.onDeleteClick(stadium.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return stadiumList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 👉 ĐÃ CẬP NHẬT: Khai báo đúng các View phẳng theo form mẫu đồng bộ
        TextView txtRowStt, txtRowMainTitle, txtRowSubValue;
        ImageView imgRowBadge, btnRowDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ khớp 100% với các ID mới dán trong item_stadium_admin.xml
            txtRowStt = itemView.findViewById(R.id.txtRowStt);
            txtRowMainTitle = itemView.findViewById(R.id.txtRowMainTitle);
            txtRowSubValue = itemView.findViewById(R.id.txtRowSubValue);
            imgRowBadge = itemView.findViewById(R.id.imgRowBadge);
            btnRowDelete = itemView.findViewById(R.id.btnRowDelete);
        }
    }
}