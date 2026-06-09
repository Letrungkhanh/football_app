package com.example.football.activities.admin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.AdminReviewAdapter;
import com.example.football.models.Review;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageReviewsActivity extends AppCompatActivity implements AdminReviewAdapter.OnReviewClickListener {

    private TextView txtManagementTitle, txtCountBronze, txtCountSilver, txtCountGold, txtCountTotal;
    private ImageView btnBackManagement, btnSearchInline;
    private LinearLayout btnFilterStatus, btnSortAZ;
    private RecyclerView recyclerViewManagementData;

    private List<Review> reviewList;
    private AdminReviewAdapter adapter;
    private DatabaseReference reviewRef;

    private List<Review> fullReviewList = new ArrayList<>();

    // 👉 CỜ THEO DÕI: Trạng thái sắp xếp (true = Mới nhất lên đầu, false = Cũ nhất lên đầu)
    private boolean isLatestSort = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reviews);

        reviewRef = FirebaseDatabase.getInstance().getReference("reviews");
        reviewList = new ArrayList<>();

        initViews();
        setupRecyclerView();

        btnBackManagement.setOnClickListener(v -> finish());

        // ==========================================
        // 🛠 1. XỬ LÝ NÚT SẮP XẾP: ĐẢO THỨ TỰ CŨ / MỚI
        // ==========================================
        btnSortAZ.setOnClickListener(v -> {
            isLatestSort = !isLatestSort; // Đảo trạng thái cờ

            Toast.makeText(this, isLatestSort ? "Đã xếp theo: Mới nhất" : "Đã xếp theo: Cũ nhất", Toast.LENGTH_SHORT).show();

            // Thực hiện sắp xếp lại mảng hiển thị theo điều kiện cờ
            sortReviewsByTime();
        });

        // ==========================================
        // 🛠 2. XỬ LÝ BỘ LỌC THEO MỨC ĐỘ SỐ SAO
        // ==========================================
        btnFilterStatus.setOnClickListener(v -> {
            String[] stars = {"Tất cả đánh giá", "Đánh giá Tệ (1-2 sao)", "Đánh giá Trung bình (3-4 sao)", "Đánh giá Tốt (5 sao)"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chọn mức đánh giá muốn lọc");
            builder.setItems(stars, (dialog, which) -> {
                List<Review> filteredList = new ArrayList<>();

                if (which == 0) {
                    filteredList.addAll(fullReviewList);
                } else if (which == 1) {
                    for (Review r : fullReviewList) {
                        if (r.getRating() <= 2) filteredList.add(r);
                    }
                } else if (which == 2) {
                    for (Review r : fullReviewList) {
                        if (r.getRating() == 3 || r.getRating() == 4) filteredList.add(r);
                    }
                } else if (which == 3) {
                    for (Review r : fullReviewList) {
                        if (r.getRating() == 5) filteredList.add(r);
                    }
                }

                reviewList.clear();
                reviewList.addAll(filteredList);
                adapter.notifyDataSetChanged();
                txtCountTotal.setText(String.valueOf(reviewList.size()));
            });
            builder.show();
        });

        // ==========================================
        // 🛠 3. TÌM KIẾM NHANH THEO TỪ KHÓA NỘI DUNG
        // ==========================================
        btnSearchInline.setOnClickListener(v -> {
            final android.widget.EditText input = new android.widget.EditText(this);
            input.setHint("Nhập từ khóa nội dung bình luận...");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tìm kiếm đánh giá");
            builder.setView(input);
            builder.setPositiveButton("Tìm kiếm", (dialog, which) -> {
                String keyword = input.getText().toString().trim();
                filterSearchReviews(keyword);
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        loadAllReviews();
    }

    private void initViews() {
        txtManagementTitle = findViewById(R.id.txtManagementTitle);
        txtCountBronze = findViewById(R.id.txtCountBronze);
        txtCountSilver = findViewById(R.id.txtCountSilver);
        txtCountGold = findViewById(R.id.txtCountGold);
        txtCountTotal = findViewById(R.id.txtCountTotal);
        btnBackManagement = findViewById(R.id.btnBackManagement);
        btnSearchInline = findViewById(R.id.btnSearchInline);
        btnFilterStatus = findViewById(R.id.btnFilterStatus);
        btnSortAZ = findViewById(R.id.btnSortAZ);
        recyclerViewManagementData = findViewById(R.id.recyclerViewManagementData);

        txtManagementTitle.setText("Quản lý đánh giá");
    }

    private void setupRecyclerView() {
        recyclerViewManagementData.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminReviewAdapter(reviewList, this);
        recyclerViewManagementData.setAdapter(adapter);
    }

    private void loadAllReviews() {
        reviewRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewList.clear();
                fullReviewList.clear();
                int countTe = 0, countOn = 0, countTot = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Review review = data.getValue(Review.class);
                    if (review != null) {
                        reviewList.add(review);
                        fullReviewList.add(review);

                        float rating = review.getRating();
                        if (rating <= 2) countTe++;
                        else if (rating == 3 || rating == 4) countOn++;
                        else countTot++;
                    }
                }

                // Luôn tự động xếp mặc định Mới nhất lên đầu khi load Firebase về
                sortReviewsByTime();

                txtCountBronze.setText(String.valueOf(countTe));
                txtCountSilver.setText(String.valueOf(countOn));
                txtCountGold.setText(String.valueOf(countTot));
                txtCountTotal.setText(String.valueOf(reviewList.size()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageReviewsActivity.this, "Lỗi Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =========================================================================
    // ⚙️ HÀM TỐI ƯU SẮP XẾP: XOAY VÒNG THEO THỜI GIAN TIMESTAMP CỦA KHÁNH
    // =========================================================================
    private void sortReviewsByTime() {
        Collections.sort(reviewList, (o1, o2) -> {
            if (isLatestSort) {
                return Long.compare(o2.getTimestamp(), o1.getTimestamp()); // Mới nhất lên đầu
            } else {
                return Long.compare(o1.getTimestamp(), o2.getTimestamp()); // Cũ nhất lên đầu
            }
        });

        // Đồng bộ mảng gốc đệm để khi dùng bộ lọc không bị xáo trộn vị trí thời gian
        Collections.sort(fullReviewList, (o1, o2) -> {
            if (isLatestSort) {
                return Long.compare(o2.getTimestamp(), o1.getTimestamp());
            } else {
                return Long.compare(o1.getTimestamp(), o2.getTimestamp());
            }
        });

        adapter.notifyDataSetChanged();
    }

    private void filterSearchReviews(String keyword) {
        List<Review> filteredList = new ArrayList<>();
        for (Review item : fullReviewList) {
            if (item.getComment() != null && item.getComment().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(item);
            }
        }
        reviewList.clear();
        reviewList.addAll(filteredList);
        adapter.notifyDataSetChanged();
        txtCountTotal.setText(String.valueOf(reviewList.size()));
    }

    @Override
    public void onDeleteClick(String reviewId) {
        if (reviewId != null && !reviewId.isEmpty()) {
            reviewRef.child(reviewId).removeValue()
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Đã xóa bình luận khỏi Firebase!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}