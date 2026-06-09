package com.example.football.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.football.R;
import com.example.football.activities.user.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminActivity extends AppCompatActivity {

    // Khai báo các nút bấm Header góc trên
    private ImageView imgAdminLogout, imgAdminScanQr;

    // 👉 ĐÃ FIX BIẾN: Đổi menuBranches thành menuBranches, và menuReview chuẩn chỉnh 8 ô
    private CardView menuCourtStatus, menuSales, menuManageServices, menuAnalytics;
    private CardView menuBranches, menuCustomers, menuReview, menuMonthlyBookings;

    // Thanh điều hướng dưới cùng
    private BottomNavigationView bottomNavigationAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        initViews();
        setupClickListeners();
        setupBottomNavigation();
    }

    private void initViews() {
        // 1. Ánh xạ các nút bấm trên thanh Header
        imgAdminLogout = findViewById(R.id.imgAdminLogout);
        imgAdminScanQr = findViewById(R.id.imgAdminScanQr);

        // 2. Ánh xạ chuẩn xác 8 ô chức năng Grid sắc màu
        menuCourtStatus = findViewById(R.id.menuCourtStatus);
        menuSales = findViewById(R.id.menuSales);
        menuManageServices = findViewById(R.id.menuManageServices);
        menuAnalytics = findViewById(R.id.menuAnalytics);
        menuBranches = findViewById(R.id.menuBranches);
        menuCustomers = findViewById(R.id.menuCustomers);

        // 👉 ĐÃ FIX ID BÁO ĐỎ: Ánh xạ chuẩn xác sang ID ô đánh giá trong XML của Khánh
        menuReview = findViewById(R.id.menuReview);

        menuMonthlyBookings = findViewById(R.id.menuMonthlyBookings);

        // 3. Ánh xạ thanh Bottom Navigation
        bottomNavigationAdmin = findViewById(R.id.bottomNavigationAdmin);
    }

    private void setupClickListeners() {
        // 👉 Nút Đăng xuất tài khoản góc trái trên cùng
        imgAdminLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, "Đã đăng xuất quyền Admin!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // 👉 Nút Quét mã QR góc phải trên cùng
        imgAdminScanQr.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng Quét mã QR kiểm tra đơn đặt sân!", Toast.LENGTH_SHORT).show();
        });

        // =========================================================================
        // BẮT SỰ KIỆN CLICK CHO 8 Ô LƯỚI CHỨC NĂNG CHUYỂN MÀN HÌNH QUẢN LÝ
        // =========================================================================

        // 1. Xem trạng thái sân
        menuCourtStatus.setOnClickListener(v -> {
            Toast.makeText(this, "Mở xem trạng thái sân thực tế", Toast.LENGTH_SHORT).show();
        });

        // 2. Bán hàng (Quầy nước/Dịch vụ tại sân)
        menuSales.setOnClickListener(v -> {
            Toast.makeText(this, "Mở tính năng Bán hàng nhanh tại quầy", Toast.LENGTH_SHORT).show();
        });

        // 3. Quản lý dịch vụ (Mở trang quản trị sân nhỏ)
        menuManageServices.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ManageCourtActivity.class));
        });

        // 4. Thống kê (Doanh thu, biểu đồ)
        // =========================================================================
        // 🎯 ĐÃ SỬA LUỒNG INTENT: Bọc Context chuẩn chỉ, bóp chết hoàn toàn lỗi NullPointerException
        // =========================================================================
        menuAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chỉ định rõ ràng AdminActivity.this làm Context để kích hoạt lệnh chuyển trang an toàn
                Intent intent = new Intent(AdminActivity.this, ManageRevenueActivity.class);
                AdminActivity.this.startActivity(intent);
            }
        });
        // =========================================================================

        // 5. Quản lý chi nhánh (Mở trang cụm sân lớn - Stadium)
        menuBranches.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ManageStadiumActivity.class));
        });

        // 6. Quản lý khách hàng (Xem danh sách tài khoản User)
        menuCustomers.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ManageUsersActivity.class));
        });

        // 👉 7. CHUYỂN ĐỔI CHUẨN XÁC: Nhảy thẳng sang trang Quản lý Đánh giá & Bình luận (Review)
        menuReview.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ManageReviewsActivity.class));
        });

        // 8. Quản lý đơn tháng (Mở trang quản lý lịch đặt - Bookings)
        menuMonthlyBookings.setOnClickListener(v -> {
            startActivity(new Intent(AdminActivity.this, ManageBookingsActivity.class));
        });
    }

    private void setupBottomNavigation() {
        bottomNavigationAdmin.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_admin_home) {
                return true;
            } else if (id == R.id.nav_admin_calendar) {
                startActivity(new Intent(AdminActivity.this, ManageBookingsActivity.class));
                return true;
            } else if (id == R.id.nav_admin_approve) {
                startActivity(new Intent(AdminActivity.this, ManageBookingsActivity.class));
                return true;
            }
            return false;
        });

        bottomNavigationAdmin.setSelectedItemId(R.id.nav_admin_home);
    }
}