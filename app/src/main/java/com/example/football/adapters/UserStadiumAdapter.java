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

public class UserStadiumAdapter extends RecyclerView.Adapter<UserStadiumAdapter.ViewHolder> {

    // Thêm final theo gợi ý của Android Studio để tối ưu hiệu năng phần cứng
    private final List<Stadium> stadiumList;
    private final OnStadiumClickListener listener;

    public interface OnStadiumClickListener {
        void onStadiumClick(Stadium stadium);
    }

    public UserStadiumAdapter(List<Stadium> stadiumList, OnStadiumClickListener listener) {
        this.stadiumList = stadiumList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stadium_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Stadium stadium = stadiumList.get(position);

        holder.txtStadiumNameUser.setText(stadium.getName());
        holder.txtStadiumAddressUser.setText(String.format("%s, %s, %s",
                stadium.getAddress(), stadium.getDistrict(), stadium.getCity()));

        // --- SỬA CHÍ MẠNG: Dùng addValueEventListener để cập nhật sao thời gian thực ---
        com.google.firebase.database.DatabaseReference reviewRef =
                com.google.firebase.database.FirebaseDatabase.getInstance().getReference("reviews");

        reviewRef.orderByChild("stadiumId").equalTo(stadium.getId())
                .addValueEventListener(new com.google.firebase.database.ValueEventListener() { // Đổi thành addValueEventListener
                    @Override
                    public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            float totalStars = 0;
                            int reviewCount = 0;

                            for (com.google.firebase.database.DataSnapshot data : snapshot.getChildren()) {
                                com.example.football.models.Review review = data.getValue(com.example.football.models.Review.class);
                                if (review != null) {
                                    totalStars += review.getRating();
                                    reviewCount++;
                                }
                            }

                            if (reviewCount > 0) {
                                float average = totalStars / reviewCount;
                                holder.txtStadiumRatingUser.setText(String.format(java.util.Locale.US, "⭐ %.1f (%d đánh giá)", average, reviewCount));
                            } else {
                                holder.txtStadiumRatingUser.setText("⭐ 0.0 (0 đánh giá)");
                            }
                        } else {
                            holder.txtStadiumRatingUser.setText("⭐ 0.0 (0 đánh giá)");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                        holder.txtStadiumRatingUser.setText("⭐ 0.0");
                    }
                });
        // --- KẾT THÚC ĐOẠN XỬ LÝ TÍNH SAO ---

        // Đoạn nạp ảnh bằng Glide giữ nguyên
        if (stadium.getImage() != null && !stadium.getImage().trim().isEmpty()) {
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(stadium.getImage().trim())
                    .placeholder(android.R.drawable.progress_horizontal)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(holder.imgStadiumUser);
        } else {
            String defaultStadiumUrl = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?q=80&w=500&auto=format&fit=crop";
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(defaultStadiumUrl)
                    .into(holder.imgStadiumUser);
        }

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
        ImageView imgStadiumUser;
        TextView txtStadiumNameUser, txtStadiumAddressUser;
        TextView txtStadiumRatingUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStadiumUser = itemView.findViewById(R.id.imgStadiumUser);
            txtStadiumNameUser = itemView.findViewById(R.id.txtStadiumNameUser);
            txtStadiumAddressUser = itemView.findViewById(R.id.txtStadiumAddressUser);
            txtStadiumRatingUser = itemView.findViewById(R.id.txtStadiumRatingUser);
        }
    }
}