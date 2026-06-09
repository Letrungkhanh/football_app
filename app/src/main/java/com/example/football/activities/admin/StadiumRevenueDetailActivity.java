package com.example.football.activities.admin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.AdminRevenueBookingAdapter;
import com.example.football.adapters.AdminRevenueCourtAdapter;
import com.example.football.models.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StadiumRevenueDetailActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView txtStadiumNameTitle, txtTotalStadiumRevenue;
    private RecyclerView rvCourtRevenue, rvBookingHistory;

    private String stadiumId, stadiumName;
    private DatabaseReference bookingRef;
    private List<Booking> matchedBookings = new ArrayList<>();
    private DecimalFormat currencyFormatter = new DecimalFormat("#,### VND");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stadium_revenue_detail);

        // Hứng mã ID từ màn hình Thống kê tổng truyền sang
        stadiumId = getIntent().getStringExtra("stadiumId");
        stadiumName = getIntent().getStringExtra("stadiumName");

        bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        initViews();
        btnBack.setOnClickListener(v -> finish());

        calculateRevenueRealtime();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtStadiumNameTitle = findViewById(R.id.txtStadiumNameTitle);
        txtTotalStadiumRevenue = findViewById(R.id.txtTotalStadiumRevenue);
        rvCourtRevenue = findViewById(R.id.rvCourtRevenue);
        rvBookingHistory = findViewById(R.id.rvBookingHistory);

        if (stadiumName != null) {
            txtStadiumNameTitle.setText(stadiumName);
        }

        rvCourtRevenue.setLayoutManager(new LinearLayoutManager(this));
        rvBookingHistory.setLayoutManager(new LinearLayoutManager(this));
    }

    private void calculateRevenueRealtime() {
        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                matchedBookings.clear();
                double totalStadiumMoney = 0;
                Map<String, Double> courtRevenueMap = new HashMap<>();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking != null) {

                        // 1. Chỉ lấy hóa đơn thuộc cụm sân này (lọc theo tên sân)
                        if (stadiumName != null && booking.getCourtName() != null &&
                                booking.getCourtName().toLowerCase().contains(stadiumName.toLowerCase())) {

                            // 2. Lọc trạng thái: CHỈ CỘNG TIỀN NẾU "Đã xác nhận"
                            String bStatus = booking.getStatus();
                            if (bStatus != null && bStatus.trim().equals("Đã xác nhận")) {
                                int price = booking.getTotalPrice();
                                totalStadiumMoney += price;

                                // Gom tiền theo từng sân con
                                String bCourtName = booking.getCourtName();
                                courtRevenueMap.put(bCourtName, courtRevenueMap.getOrDefault(bCourtName, 0.0) + price);
                            }

                            // 3. Đẩy tất cả đơn (kể cả chờ/huỷ) vào danh sách đối soát dưới đáy để admin dễ theo dõi
                            matchedBookings.add(booking);
                        }
                    }
                }

                // Cập nhật tổng tiền lên màn hình
                if (txtTotalStadiumRevenue != null) {
                    txtTotalStadiumRevenue.setText(currencyFormatter.format(totalStadiumMoney));
                }

                updateAdapters(courtRevenueMap, matchedBookings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void updateAdapters(Map<String, Double> courtRevenueMap, List<Booking> bookingList) {
        // 1. Đổ dữ liệu phân loại tiền của sân con lên Adapter 1
        AdminRevenueCourtAdapter courtAdapter = new AdminRevenueCourtAdapter(courtRevenueMap);
        rvCourtRevenue.setAdapter(courtAdapter);

        // 2. Đổ danh sách hóa đơn lịch sử đối soát lên Adapter 2 dưới đáy
        AdminRevenueBookingAdapter bookingAdapter = new AdminRevenueBookingAdapter(bookingList);
        rvBookingHistory.setAdapter(bookingAdapter);
    }
}