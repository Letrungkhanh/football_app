package com.example.football.activities.user;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // ĐÃ THÊM: Để nhận diện nút Back ImageView
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.ReviewAdapter;
import com.example.football.adapters.UserCourtAdapter;
import com.example.football.models.Court;
import com.example.football.models.Review;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserSelectCourtActivity extends AppCompatActivity implements UserCourtAdapter.OnCourtClickListener {

    private RecyclerView recyclerViewUserCourts, recyclerViewReviews;
    private List<Court> courtList;
    private List<Review> reviewList;
    private UserCourtAdapter courtAdapter;
    private ReviewAdapter reviewAdapter;
    private DatabaseReference courtRef, reviewRef;
    private String targetStadiumId = "";
    private String targetStadiumName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_select_court);

        if (getIntent() != null) {
            targetStadiumId = getIntent().getStringExtra("stadiumId");
            targetStadiumName = getIntent().getStringExtra("stadiumName");
        }

        // 👉 ĐÃ THÊM: Ánh xạ nút quay lại Custom và xử lý đóng màn hình
        ImageView btnBackSelectCourt = findViewById(R.id.btnBackSelectCourt);
        btnBackSelectCourt.setOnClickListener(v -> finish());

        TextView txtHeader = findViewById(R.id.txtHeaderStadiumName);
        if (targetStadiumName != null) {
            txtHeader.setText(String.format("Hệ thống: %s", targetStadiumName));
        }

        // Cấu hình danh sách Sân nhỏ
        courtRef = FirebaseDatabase.getInstance().getReference("courts");
        courtList = new ArrayList<>();
        recyclerViewUserCourts = findViewById(R.id.recyclerViewUserCourts);
        recyclerViewUserCourts.setLayoutManager(new LinearLayoutManager(this));
        courtAdapter = new UserCourtAdapter(courtList, this);
        recyclerViewUserCourts.setAdapter(courtAdapter);

        // Cấu hình danh sách Bình luận
        reviewRef = FirebaseDatabase.getInstance().getReference("reviews");
        reviewList = new ArrayList<>();
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerViewReviews.setAdapter(reviewAdapter);

        Button btnOpenRating = findViewById(R.id.btnOpenRatingDialog);
        btnOpenRating.setOnClickListener(v -> showRatingDialog());

        loadCourtsByStadium();
        loadReviewsByStadium();
    }

    private void loadCourtsByStadium() {
        if (targetStadiumId == null || targetStadiumId.isEmpty()) return;

        courtRef.orderByChild("stadiumId").equalTo(targetStadiumId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        courtList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Court court = data.getValue(Court.class);
                            if (court != null) {
                                court.setId(data.getKey());
                                courtList.add(court);
                            }
                        }
                        courtAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserSelectCourtActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadReviewsByStadium() {
        if (targetStadiumId == null || targetStadiumId.isEmpty()) return;

        reviewRef.orderByChild("stadiumId").equalTo(targetStadiumId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        reviewList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            Review review = data.getValue(Review.class);
                            if (review != null) {
                                reviewList.add(review);
                            }
                        }
                        reviewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(UserSelectCourtActivity.this, "Lỗi tải bình luận!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRatingDialog() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đánh giá!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_rating, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        RatingBar ratingBar = view.findViewById(R.id.ratingBar);
        EditText edtComment = view.findViewById(R.id.edtReviewComment);
        Button btnSubmit = view.findViewById(R.id.btnSubmitReview);

        btnSubmit.setOnClickListener(v -> {
            float stars = ratingBar.getRating();
            String comment = edtComment.getText().toString().trim();

            if (stars == 0) {
                Toast.makeText(this, "Vui lòng chọn số sao đánh giá!", Toast.LENGTH_SHORT).show();
                return;
            }

            String reviewId = reviewRef.push().getKey();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String userName = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (userName != null && userName.contains("@")) {
                userName = userName.split("@")[0];
            }

            Review review = new Review(reviewId, targetStadiumId, userId, userName, stars, comment, System.currentTimeMillis());

            if (reviewId != null) {
                reviewRef.child(reviewId).setValue(review).addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Cảm ơn bạn đã gửi đánh giá!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                });
            }
        });

        dialog.show();
    }

    @Override
    public void onBookClick(Court court) {
        // Đoạn click chuyển màn hình ở Activity trước phải chuẩn như thế này:
        Intent intent = new Intent(UserSelectCourtActivity.this, BookingActivity.class);
        intent.putExtra("courtId", court.getId()); // 👉 Phải truyền đúng chuỗi "court3" sang nha!
        intent.putExtra("courtName", court.getName());
        intent.putExtra("courtPrice", court.getPrice());
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}