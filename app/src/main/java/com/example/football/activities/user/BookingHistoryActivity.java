package com.example.football.activities.user;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.BookingHistoryAdapter;
import com.example.football.models.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerViewHistory;
    private BookingHistoryAdapter historyAdapter;
    private List<Booking> bookingList;
    private DatabaseReference bookingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        Toolbar toolbar = findViewById(R.id.toolbarHistory);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);

        // 👉 ĐÃ TỐI ƯU: Thiết lập LinearLayoutManager chuẩn để chạy mượt trong NestedScrollView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewHistory.setLayoutManager(layoutManager);
        recyclerViewHistory.setHasFixedSize(true);
        recyclerViewHistory.setNestedScrollingEnabled(false); // Khóa cuộn của RV để nhường cho NestedScrollView bọc ngoài

        bookingList = new ArrayList<>();
        historyAdapter = new BookingHistoryAdapter(bookingList);
        recyclerViewHistory.setAdapter(historyAdapter);

        bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        loadBookingHistory();
    }

    private void loadBookingHistory() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) return;

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // CHỈ LỌC RA ĐƠN ĐẶT SÂN CỦA USER ĐANG ĐĂNG NHẬP
        bookingRef.orderByChild("userId").equalTo(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bookingList.clear();
                        for (DataSnapshot data : snapshot.getChildren()) {
                            // 👉 ĐÃ BỌC TRY-CATCH: Chống sập app tuyệt đối nếu lỡ có đơn đặt sân cũ bị lỗi cấu hình dữ liệu bẩn
                            try {
                                Booking booking = data.getValue(Booking.class);
                                if (booking != null) {
                                    bookingList.add(booking);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        historyAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(BookingHistoryActivity.this, "Lỗi tải lịch sử phủi!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}