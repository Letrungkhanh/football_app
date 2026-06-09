package com.example.football.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.football.R;
import com.example.football.models.Booking;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView txtCourtName, txtDateTime, txtTotalAmount, txtQrHint;
    private RadioGroup radioGroupPayment;
    private LinearLayout layoutQrContainer;
    private ImageView imgQrCode, btnBackPayment;
    private Button btnConfirm;

    private String bookingId, courtId, courtName, userId, userPhone, bookingDate, timeSlot;
    private int courtPrice;
    private String currentMethod = "Tiền mặt tại sân";

    // Biến lưu chuỗi danh sách dịch vụ nước uống lấy từ Firebase
    private String selectedServices = "Không chọn dịch vụ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // 1. Hứng trọn gói dữ liệu đơn hàng được truyền sang từ BookingActivity trước
        if (getIntent() != null) {
            bookingId = getIntent().getStringExtra("bookingId");
            courtId = getIntent().getStringExtra("courtId");
            courtName = getIntent().getStringExtra("courtName");
            userId = getIntent().getStringExtra("userId");
            userPhone = getIntent().getStringExtra("userPhone");
            bookingDate = getIntent().getStringExtra("bookingDate");
            timeSlot = getIntent().getStringExtra("timeSlot");
            courtPrice = getIntent().getIntExtra("courtPrice", 0);

            selectedServices = getIntent().getStringExtra("selectedServices");
            if (selectedServices == null || selectedServices.isEmpty()) {
                selectedServices = "Không chọn dịch vụ";
            }
        }

        // 2. Gọi hàm ánh xạ giao diện
        initViews();

        // 3. Lắng nghe sự kiện đổi phương thức thanh toán để ẩn/hiện QR linh hoạt
        radioGroupPayment.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioCod) {
                currentMethod = "Tiền mặt tại sân";
                layoutQrContainer.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioQr) {
                currentMethod = "Chuyển khoản QR";
                layoutQrContainer.setVisibility(View.VISIBLE);

                if (bookingId != null && bookingId.length() >= 6) {
                    String shortId = bookingId.substring(0, 6).toUpperCase(Locale.ROOT);
                    txtQrHint.setText(String.format("Nội dung CK: DATSAN %s", shortId));

                    String bankCode = "MB";
                    String bankAccount = "0987654321";
                    String transferInfo = "DATSAN " + shortId;

                    // Mã QR tự động cộng cả tiền nước uống
                    String vietQrUrl = "https://img.vietqr.io/image/" + bankCode + "-" + bankAccount + "-compact2.jpg"
                            + "?amount=" + courtPrice
                            + "&addInfo=" + transferInfo;

                    Glide.with(PaymentActivity.this)
                            .load(vietQrUrl)
                            .placeholder(android.R.drawable.progress_horizontal)
                            .error(android.R.drawable.stat_notify_error)
                            .into(imgQrCode);

                } else {
                    txtQrHint.setText("Nội dung CK: DATSAN ONLINE");
                }
            }
        });

        // Bấm nút chốt đơn đẩy lên Realtime Database
        btnConfirm.setOnClickListener(v -> saveBookingToFirebase());
    }

    private void initViews() {
        txtCourtName = findViewById(R.id.txtPayCourtName);
        txtDateTime = findViewById(R.id.txtPayDateTime);
        txtTotalAmount = findViewById(R.id.txtPayTotalAmount);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        layoutQrContainer = findViewById(R.id.layoutQrContainer);
        imgQrCode = findViewById(R.id.imgQrCodePayment);
        txtQrHint = findViewById(R.id.txtQrContentHint);
        btnConfirm = findViewById(R.id.btnFinalizePayment);

        btnBackPayment = findViewById(R.id.btnBackPayment);
        btnBackPayment.setOnClickListener(v -> finish());

        // Đổ dữ liệu lên giao diện
        txtCourtName.setText(courtName);
        txtDateTime.setText(String.format("Lịch đá: %s | Khung giờ: %s", bookingDate, timeSlot));
        txtTotalAmount.setText(String.format(Locale.GERMANY, "%,d đ", courtPrice));
    }

    private void saveBookingToFirebase() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("bookings");
        DatabaseReference courtRef = FirebaseDatabase.getInstance().getReference("courts").child(courtId);

        // Lấy stadiumId từ node courts trước khi lưu booking
        courtRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                String stadiumId = snapshot.child("stadiumId").getValue(String.class);

                // Tạo Map với stadiumId đã lấy được
                java.util.Map<String, Object> bookingMap = new java.util.HashMap<>();
                bookingMap.put("id", bookingId);
                bookingMap.put("stadiumId", stadiumId != null ? stadiumId : "unknown");
                bookingMap.put("courtId", courtId);
                bookingMap.put("courtName", courtName);
                bookingMap.put("userId", userId);
                bookingMap.put("userPhone", userPhone);
                bookingMap.put("date", bookingDate);
                bookingMap.put("slotTime", timeSlot);
                bookingMap.put("totalPrice", courtPrice);
                bookingMap.put("status", "Chờ xác nhận");
                bookingMap.put("createdAt", System.currentTimeMillis());
                bookingMap.put("selectedServices", selectedServices);

                // Đẩy lên Firebase
                dbRef.child(bookingId).setValue(bookingMap).addOnSuccessListener(unused -> {
                    Toast.makeText(PaymentActivity.this, "Đặt sân thành công!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(PaymentActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}