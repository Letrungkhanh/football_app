package com.example.football.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.AdminRevenueStadiumAdapter;
import com.example.football.models.Booking;
import com.example.football.models.Stadium;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageRevenueActivity extends AppCompatActivity implements AdminRevenueStadiumAdapter.OnStadiumClickListener {

    private ImageView btnBack;
    private String userRole = ""; // ĐÃ KHAI BÁO TOÀN CỤC
    private TextView txtTotalSystemRevenue;
    private RecyclerView rvStadiumRevenue;

    private List<Stadium> stadiumList = new ArrayList<>();
    private AdminRevenueStadiumAdapter adapter;
    private DatabaseReference stadiumRef, bookingRef;

    private Map<String, Double> revenueMap = new HashMap<>();
    private DecimalFormat formatter = new DecimalFormat("#,### VND");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stadium);

        stadiumRef = FirebaseDatabase.getInstance().getReference("stadiums");
        bookingRef = FirebaseDatabase.getInstance().getReference("bookings");

        initViews();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBackManagement);
        if (btnBack == null) btnBack = findViewById(R.id.btnBack);
        rvStadiumRevenue = findViewById(R.id.recyclerViewManagementData);
        txtTotalSystemRevenue = findViewById(R.id.txtCountTotal);

        if (rvStadiumRevenue != null) {
            rvStadiumRevenue.setLayoutManager(new LinearLayoutManager(this));
            adapter = new AdminRevenueStadiumAdapter(stadiumList, this);
            rvStadiumRevenue.setAdapter(adapter);
        }
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void loadStadiums() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                userRole = userSnapshot.child("role").getValue(String.class);

                // SỬA: dùng userRole ở đây
                Query query = (userRole != null && userRole.equalsIgnoreCase("admin"))
                        ? stadiumRef
                        : stadiumRef.orderByChild("ownerId").equalTo(currentUserId); // Lọc theo UID chính chủ

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        processStadiumSnapshot(snapshot);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void processStadiumSnapshot(DataSnapshot snapshot) {
        stadiumList.clear();
        for (DataSnapshot data : snapshot.getChildren()) {
            Stadium stadium = data.getValue(Stadium.class);
            if (stadium != null) {
                stadium.setId(data.getKey());
                stadiumList.add(stadium);
            }
        }
        adapter.notifyDataSetChanged();
        loadRevenueData();
    }

    private void loadRevenueData() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        bookingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot bookingSnapshot) {
                revenueMap.clear();
                double totalRevenue = 0;

                for (DataSnapshot data : bookingSnapshot.getChildren()) {
                    Booking booking = data.getValue(Booking.class);
                    if (booking == null || !"Đã xác nhận".equals(booking.getStatus())) continue;

                    for (Stadium s : stadiumList) {
                        if (s.getId().equals(booking.getStadiumId())) {
                            // SỬA: Dùng userRole và currentUserId
                            boolean isAuthorized = "admin".equalsIgnoreCase(userRole) ||
                                    currentUserId.equals(s.getOwnerId());

                            if (isAuthorized) {
                                double price = booking.getTotalPrice();
                                totalRevenue += price;
                                revenueMap.put(s.getId(), revenueMap.getOrDefault(s.getId(), 0.0) + price);
                            }
                        }
                    }
                }

                if (txtTotalSystemRevenue != null) {
                    txtTotalSystemRevenue.setText(formatter.format(totalRevenue) + " VND");
                }

                for (Stadium s : stadiumList) {
                    // SỬA: dùng userRole ở đây
                    if ("admin".equalsIgnoreCase(userRole) || currentUserId.equals(s.getOwnerId())) {
                        double rev = revenueMap.getOrDefault(s.getId(), 0.0);
                        s.setAddress("Doanh thu: " + formatter.format(rev));
                    } else {
                        s.setAddress("Không có quyền xem doanh thu");
                    }
                }
                adapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStadiums();
    }

    @Override
    public void onStadiumClick(Stadium stadium) {
        Intent intent = new Intent(this, StadiumRevenueDetailActivity.class);
        intent.putExtra("stadiumId", stadium.getId());
        intent.putExtra("stadiumName", stadium.getName());
        startActivity(intent);
    }
}