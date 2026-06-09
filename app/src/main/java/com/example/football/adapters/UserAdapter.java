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
import com.example.football.models.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<User> userList;
    private final OnUserClickListener listener;

    public interface OnUserClickListener {
        void onToggleRoleClick(User user);
    }

    public UserAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 👉 ĐÃ ĐỒNG BỘ: Gọi đúng file layout item dòng của User (Khánh check xem tên file XML của bạn là item_user hay item_user_admin nhé)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);

        // 1. Gán số thứ tự tự động tăng cho cột STT (Bắt đầu từ 1)
        holder.txtRowStt.setText(String.valueOf(position + 1));

        // 2. Đổ tên khách hàng lên cột Tên
        holder.txtRowMainTitle.setText(user.getName());

        // 3. Đổ quyền hạn (Admin / Thành viên) lên cột Xếp hạng
        String role = user.getRole();
        if ("admin".equalsIgnoreCase(role)) {
            holder.txtRowSubValue.setText("Admin");
            // 🔴 Nếu là Admin -> Đổi ngôi sao huy hiệu sang màu Đỏ Rực rỡ quý tộc
            holder.imgRowBadge.setColorFilter(Color.parseColor("#EF4444"));
        } else {
            holder.txtRowSubValue.setText("Thành viên");
            // 🟢 Nếu là User thường -> Đổi ngôi sao huy hiệu sang màu Xanh Lá mượt mà
            holder.imgRowBadge.setColorFilter(Color.parseColor("#4ADE80"));
        }

        // 4. Bắt sự kiện click vào nút Xoay đảo quyền màu cam cháy máy ở rìa phải
        holder.btnRowDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleRoleClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 👉 ĐÃ CHUYỂN ĐỔI THÀNH CÁC BIẾN FLAT MỚI TINH ĐỒNG BỘ ALOBO
        TextView txtRowStt, txtRowMainTitle, txtRowSubValue;
        ImageView imgRowBadge, btnRowDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ khớp 100% với các ID phẳng trong file XML mới
            txtRowStt = itemView.findViewById(R.id.txtRowStt);
            txtRowMainTitle = itemView.findViewById(R.id.txtRowMainTitle);
            txtRowSubValue = itemView.findViewById(R.id.txtRowSubValue);
            imgRowBadge = itemView.findViewById(R.id.imgRowBadge);
            btnRowDelete = itemView.findViewById(R.id.btnRowDelete); // Nút xoay đảo vị trí chức vụ
        }
    }
}