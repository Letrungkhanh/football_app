package com.example.football.activities.owner;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.football.R;
import com.example.football.activities.admin.*;
import com.example.football.activities.user.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import android.widget.ImageView;

public class OwnerActivity extends AppCompatActivity {

    private CardView menuMyStadiums, menuMyRevenue, menuMyBookings, menuMyReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        // Khởi tạo các View
        menuMyStadiums = findViewById(R.id.menuMyStadiums);
        menuMyRevenue = findViewById(R.id.menuMyRevenue);
        menuMyBookings = findViewById(R.id.menuMyBookings);
        menuMyReviews = findViewById(R.id.menuMyReviews);

        // Gán sự kiện
        menuMyStadiums.setOnClickListener(v -> navigateTo(ManageStadiumActivity.class));
        menuMyRevenue.setOnClickListener(v -> navigateTo(ManageRevenueActivity.class));
        menuMyBookings.setOnClickListener(v -> navigateTo(ManageBookingsActivity.class));
        menuMyReviews.setOnClickListener(v -> navigateTo(ManageReviewsActivity.class));
        ImageView imgLogout = findViewById(R.id.imgOwnerLogout);
        imgLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            // Xóa hết các màn hình cũ để không bấm nút Back quay lại được
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }


    private void navigateTo(Class<?> activityClass) {
        Intent intent = new Intent(OwnerActivity.this, activityClass);
        intent.putExtra("isOwnerView", true); // Đánh dấu đây là chế độ Chủ sân
        startActivity(intent);
    }
}