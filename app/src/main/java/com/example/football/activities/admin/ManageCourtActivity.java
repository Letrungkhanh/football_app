package com.example.football.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import com.example.football.R;
import com.example.football.adapters.CourtAdapter;
import com.example.football.models.Court;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageCourtActivity extends AppCompatActivity implements CourtAdapter.OnCourtClickListener {

    private TextView txtManagementTitle, txtCurrentBranchName;
    private ImageView btnBackManagement, btnSearchInline;
    private LinearLayout btnFilterStatus, btnSortAZ;
    private RecyclerView recyclerViewManagementData;

    private TextView txtCountBronze, txtCountSilver, txtCountGold, txtCountTotal;

    private List<Court> courtList;
    private CourtAdapter courtAdapter;
    private DatabaseReference courtRef;

    private String targetStadiumId = "";
    private String targetStadiumName = "";

    // 👉 Danh sách đệm lưu trữ bản gốc dữ liệu sau khi lọc chi nhánh để phục vụ search/filter
    private List<Court> fullCourtList = new ArrayList<>();
    private boolean isAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_court);

        if (getIntent() != null) {
            targetStadiumId = getIntent().getStringExtra("stadiumId");
            targetStadiumName = getIntent().getStringExtra("stadiumName");
        }

        courtRef = FirebaseDatabase.getInstance().getReference("courts");
        courtList = new ArrayList<>();

        initViews();
        setupRecyclerView();

        btnBackManagement.setOnClickListener(v -> finish());

        btnSearchInline.setOnClickListener(v -> {
            Intent intent = new Intent(ManageCourtActivity.this, AddEditCourtActivity.class);
            intent.putExtra("stadiumId", targetStadiumId);
            startActivity(intent);
        });

        // 👉 ĐÃ FIX LOGIC TOAST: Hiển thị đúng chu kỳ trạng thái thực tế khi Admin click
        btnSortAZ.setOnClickListener(v -> {
            Toast.makeText(this, isAscending ? "Đã sắp xếp A -> Z" : "Đã sắp xếp Z -> A", Toast.LENGTH_SHORT).show();
            sortDataAlphabetically();
        });

// 👉 ĐÃ SỬA: Gọi trực tiếp qua AlertDialog rút gọn, sạch bóng lỗi đỏ method
        btnFilterStatus.setOnClickListener(v -> {
            String[] items = {"Tất cả sân", "Sân 5 người", "Sân 7 người", "Sân 11 người"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chọn loại sân muốn lọc");
            builder.setItems(items, (dialog, which) -> {
                List<Court> filteredList = new ArrayList<>();

                if (which == 0) {
                    filteredList.addAll(fullCourtList);
                } else if (which == 1) {
                    for (Court c : fullCourtList) {
                        if ("5 người".equals(c.getType())) filteredList.add(c);
                    }
                } else if (which == 2) {
                    for (Court c : fullCourtList) {
                        if ("7 người".equals(c.getType())) filteredList.add(c);
                    }
                } else if (which == 3) {
                    for (Court c : fullCourtList) {
                        if ("11 người".equals(c.getType())) filteredList.add(c);
                    }
                }

                courtList.clear();
                courtList.addAll(filteredList);
                courtAdapter.notifyDataSetChanged();

                txtCountTotal.setText(String.valueOf(courtList.size()));
            });
            builder.show();
        });

        loadCourtsDataByStadium();
    }

    private void initViews() {
        txtManagementTitle = findViewById(R.id.txtManagementTitle);
        txtCurrentBranchName = findViewById(R.id.txtCurrentBranchName);
        btnBackManagement = findViewById(R.id.btnBackManagement);
        btnSearchInline = findViewById(R.id.btnSearchInline);
        btnFilterStatus = findViewById(R.id.btnFilterStatus);
        btnSortAZ = findViewById(R.id.btnSortAZ);
        recyclerViewManagementData = findViewById(R.id.recyclerViewManagementData);

        txtCountBronze = findViewById(R.id.txtCountBronze);
        txtCountSilver = findViewById(R.id.txtCountSilver);
        txtCountGold = findViewById(R.id.txtCountGold);
        txtCountTotal = findViewById(R.id.txtCountTotal);

        txtManagementTitle.setText("Quản lý sân bóng");

        if (targetStadiumName != null && !targetStadiumName.isEmpty()) {
            txtCurrentBranchName.setText(targetStadiumName);
        } else {
            txtCurrentBranchName.setText("Tất cả chi nhánh");
        }
    }

    private void setupRecyclerView() {
        recyclerViewManagementData.setLayoutManager(new LinearLayoutManager(this));
        courtAdapter = new CourtAdapter(courtList, this);
        recyclerViewManagementData.setAdapter(courtAdapter);
    }

    private void loadCourtsDataByStadium() {
        courtRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                courtList.clear();
                fullCourtList.clear(); // Dọn sạch mảng đệm bản gốc
                int countSan5 = 0, countSan7 = 0, countSan11 = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    Court court = data.getValue(Court.class);
                    if (court != null) {
                        court.setId(data.getKey());

                        // 👉 ĐÃ FIX TRIỆT ĐỂ: Luồng kiểm tra điều kiện bốc tách dữ liệu chi nhánh đồng bộ không trùng lặp
                        if (targetStadiumId == null || targetStadiumId.trim().isEmpty() || targetStadiumId.equals(court.getStadiumId())) {
                            courtList.add(court);
                            fullCourtList.add(court); // Chỉ lưu các sân thuộc chi nhánh được chọn vào bộ nhớ đệm lọc

                            if ("5 người".equals(court.getType())) countSan5++;
                            else if ("7 người".equals(court.getType())) countSan7++;
                            else countSan11++;
                        }
                    }
                }

                txtCountBronze.setText(String.valueOf(countSan5));
                txtCountSilver.setText(String.valueOf(countSan7));
                txtCountGold.setText(String.valueOf(countSan11));
                txtCountTotal.setText(String.valueOf(courtList.size()));

                courtAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageCourtActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditClick(Court court) {
        Intent intent = new Intent(ManageCourtActivity.this, AddEditCourtActivity.class);
        intent.putExtra("courtId", court.getId());
        intent.putExtra("stadiumId", targetStadiumId);
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(String courtId) {
        courtRef.child(courtId).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(ManageCourtActivity.this, "Xóa sân thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ManageCourtActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    // Hàm tìm kiếm nhanh theo tên phẳng lí (Khánh có thể gọi khi tích hợp thêm ô EditText nhập liệu nếu cần)
    private void filterSearch(String text){
        List<Court> filteredList = new ArrayList<>();
        for (Court item : fullCourtList){
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        courtList.clear();
        courtList.addAll(filteredList);
        courtAdapter.notifyDataSetChanged();
        txtCountTotal.setText(String.valueOf(courtList.size()));
    }

    private void sortDataAlphabetically() {
        Collections.sort(courtList, (o1, o2) -> {
            if (isAscending) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            } else {
                return o2.getName().compareToIgnoreCase(o1.getName());
            }
        });

        isAscending = !isAscending;
        courtAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCourtClick(Court court) {}
}