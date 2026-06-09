package com.example.football.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.models.Review;

import java.util.List;

public class AdminReviewAdapter extends RecyclerView.Adapter<AdminReviewAdapter.ViewHolder> {

    private final List<Review> reviewList;
    private final OnReviewClickListener listener;

    public interface OnReviewClickListener {
        void onDeleteClick(String reviewId);
    }

    public AdminReviewAdapter(List<Review> reviewList, OnReviewClickListener listener) {
        this.reviewList = reviewList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 💡 MẸO KIỂM TRA: Nếu code XML phẳng bạn lưu ở file item_review_admin.xml
        // Hãy đổi chữ R.layout.item_review thành R.layout.item_review_admin cho đúng nhé!
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewList.get(position);

        if (holder.txtRowStt != null) {
            holder.txtRowStt.setText(String.valueOf(position + 1));
        }

        // 1. ĐỔ TÊN NGƯỜI BÌNH LUẬN (Bốc đúng trường userName từ Firebase)
        if (holder.txtReviewUser != null) {
            if (review.getUserName() != null && !review.getUserName().isEmpty()) {
                // 👉 SỬA TẠI ĐÂY: Thay vì để chữ tĩnh "Lê Trung Khánh", nạp biến từ Firebase
                holder.txtReviewUser.setText(review.getUserName());
            } else {
                holder.txtReviewUser.setText("Khách hàng Alobo");
            }
        }

        // 2. ĐỔ NỘI DUNG BÌNH LUẬN CHUẨN REALTIME
        if (holder.txtReviewContent != null) {
            // 👉 SỬA TẠI ĐÂY: Khánh check xem có đang bị viết lộn chữ tiếng Việt tĩnh nào ở đây không nhé
            if (review.getComment() != null && !review.getComment().isEmpty()) {
                holder.txtReviewContent.setText(review.getComment()); // Bốc chuẩn dòng "san co rat dep nhe"
            } else {
                holder.txtReviewContent.setText("Không có nội dung bình luận.");
            }
        }

        // 3. ĐỔ CHỮ SỐ SAO (Hiện tại máy ảo đang bị trống phần chữ số)
        if (holder.txtReviewRating != null) {
            holder.txtReviewRating.setVisibility(View.VISIBLE); // Ép hiện TextView số sao lên

            // Ép kiểu float rating (5.0 hoặc 4.0) về dạng int (5 hoặc 4) để hiển thị tăm tắp
            int starCount = (int) review.getRating();
            holder.txtReviewRating.setText(String.valueOf(starCount));
        }

        if (holder.btnRowDelete != null) {
            holder.btnRowDelete.setOnClickListener(v -> {
                if (listener != null && review.getReviewId() != null) {
                    listener.onDeleteClick(review.getReviewId());
                }
            });
        }
    }
    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRowStt, txtReviewUser, txtReviewContent, txtReviewRating;
        ImageView btnRowDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRowStt = itemView.findViewById(R.id.txtRowStt);
            txtReviewUser = itemView.findViewById(R.id.txtReviewUser);
            txtReviewContent = itemView.findViewById(R.id.txtReviewContent);
            txtReviewRating = itemView.findViewById(R.id.txtReviewRating);
            btnRowDelete = itemView.findViewById(R.id.btnRowDelete);
        }
    }
}