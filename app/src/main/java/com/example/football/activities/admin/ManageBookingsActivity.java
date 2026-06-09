package com.example.football.activities.admin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Thư viện hiển thị Dialog lọc phẳng
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.AdminBookingAdapter;
import com.example.football.models.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageBookingsActivity extends AppCompatActivity implements AdminBookingAdapter.OnBookingClickListener {

    private TextView txtManagementTitle, txtCountBronze, txtCountSilver, txtCountGold, txtCountTotal;
    private ImageView btnBackManagement, btnSearchInline;
    private LinearLayout btnFilterStatus, btnSortAZ;
    private RecyclerView recyclerViewManagementData;

    private List<Booking> bookingList;
    private AdminBookingAdapter adapter;
    private DatabaseReference bookingRef;

    // 👉 DANH SÁCH ĐỆM: Backup toàn bộ đơn đặt sân từ Firebase phục vụ luồng lọc mảng
    private List<Booking> fullBookingList = new ArrayList<>();
    private boolean isLatestSort = true; // Cờ theo dõi sắp xếp đơn cũ/mới

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        bookingRef = FirebaseDatabase.getInstance().getReference("bookings");
        bookingList = new ArrayList<>();

        initViews();
        setupRecyclerView();

        btnBackManagement.setOnClickListener(v -> finish());

        // ==========================================
        // 🛠 1. LOGIC SẮP XẾP ĐƠN ĐẶT SÂN THEO THỜI GIAN
        // ==========================================
        btnSortAZ.setOnClickListener(v -> {
            Toast.makeText(this, isLatestSort ? "Đã đảo thứ tự danh sách đơn" : "Đã khôi phục thứ tự đơn", Toast.LENGTH_SHORT).show();
            // Đảo ngược danh sách đơn hàng nhanh chóng
            Collections.reverse(bookingList);
            isLatestSort = !isLatestSort;
            adapter.notifyDataSetChanged();
        });

        // ==========================================
        // 🛠 2. LOGIC LỌC ĐƠN THEO TRẠNG THÁI (CHỐT HẠ PHÂN HỆ)
        // ==========================================
        btnFilterStatus.setOnClickListener(v -> {
            String[] statuses = {"Tất cả đơn", "Chờ duyệt", "Đã xác nhận", "Đã hủy"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chọn trạng thái đơn muốn lọc");
            builder.setItems(statuses, (dialog, which) -> {
                List<Booking> filteredList = new ArrayList<>();

                if (which == 0) { // Chọn "Tất cả đơn" -> Đổ lại toàn bộ mảng đệm backup vào
                    filteredList.addAll(fullBookingList);
                } else {
                    String selectedStatus = statuses[which];
                    for (Booking b : fullBookingList) {
                        // So sánh thuộc tính Status của model Booking
                        if (b.getStatus() != null && b.getStatus().equalsIgnoreCase(selectedStatus)) {
                            filteredList.add(b);
                        }
                    }
                }

                // Cập nhật lại giỏ hiển thị trên màn hình Admin
                bookingList.clear();
                bookingList.addAll(filteredList);
                adapter.notifyDataSetChanged();

                // Cập nhật số đếm tổng số lượng đơn sau khi lọc trên huy chương xanh lá đầu trang
                txtCountTotal.setText(String.valueOf(bookingList.size()));
            });
            builder.show();
        });

        // ==========================================
        // 🛠 3. NÚT KÍNH LÚP: TÌM KIẾM ĐƠN THEO SỐ ĐIỆN THOẠI KHÁCH
        // ==========================================
        btnSearchInline.setOnClickListener(v -> {
            final android.widget.EditText input = new android.widget.EditText(this);
            input.setHint("Nhập số điện thoại khách đặt sân...");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tìm kiếm đơn hàng");
            builder.setView(input);
            builder.setPositiveButton("Tìm kiếm", (dialog, which) -> {
                String phoneKeyword = input.getText().toString().trim();
                filterSearchBookingsByPhone(phoneKeyword);
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        loadAllBookings();
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

        txtManagementTitle.setText("Quản lý đơn đặt sân");
    }

    private void setupRecyclerView() {
        recyclerViewManagementData.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminBookingAdapter(bookingList, this);
        recyclerViewManagementData.setAdapter(adapter);
    }

    private void loadAllBookings() {
        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingList.clear();
                fullBookingList.clear(); // Dọn sạch mảng đệm bản gốc
                int countChoDuyet = 0, countDaDuyet = 0, countDaHuy = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking != null) {
                        booking.setId(data.getKey());

                        // Đổ dữ liệu song song vào cả danh sách chạy và danh sách đệm gốc
                        bookingList.add(booking);
                        fullBookingList.add(booking);

                        // Tính toán đếm số lượng phân loại tự động
                        String status = booking.getStatus();
                        if ("Đã xác nhận".equals(status)) countDaDuyet++;
                        else if ("Đã hủy".equals(status)) countDaHuy++;
                        else countChoDuyet++;
                    }
                }

                // Cập nhật số liệu Realtime lên các ô màu sắc ở Header
                txtCountBronze.setText(String.valueOf(countChoDuyet));
                txtCountSilver.setText(String.valueOf(countDaDuyet));
                txtCountGold.setText(String.valueOf(countDaHuy));
                txtCountTotal.setText(String.valueOf(bookingList.size()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageBookingsActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ===================================================
    // THUẬT TOÁN TÌM KIẾM ĐƠN ĐẶT SÂN CHUẨN THEO SỐ ĐIỆN THOẠI
    // ===================================================
    private void filterSearchBookingsByPhone(String phone) {
        List<Booking> filteredList = new ArrayList<>();
        for (Booking item : fullBookingList) {
            // So khớp chuỗi SĐT khách đặt (không lo phân biệt chữ hoa/thường)
            if (item.getUserPhone() != null && item.getUserPhone().contains(phone)) {
                filteredList.add(item);
            }
        }
        bookingList.clear();
        bookingList.addAll(filteredList);
        adapter.notifyDataSetChanged();
        txtCountTotal.setText(String.valueOf(bookingList.size()));
    }

    @Override
    public void onRowActionClick(Booking booking) {
        String currentStatus = booking.getStatus();
        String nextStatus;

        if ("Đã xác nhận".equals(currentStatus)) {
            nextStatus = "Đã hủy";
        } else if ("Đã hủy".equals(currentStatus)) {
            nextStatus = "Chờ duyệt";
        } else {
            nextStatus = "Đã xác nhận";
        }

        if (booking.getId() != null) {
            bookingRef.child(booking.getId()).child("status").setValue(nextStatus)
                    .addOnSuccessListener(unused -> Toast.makeText(this, "Cập nhật đơn: " + nextStatus, Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}