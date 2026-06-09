package com.example.football.activities.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // 👉 ĐÃ THÊM: Import cứu cánh dọn sạch lỗi Dialog
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.AdminStadiumAdapter;
import com.example.football.models.Stadium;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth; // 👉 Import cho FirebaseAuth
import com.google.firebase.database.Query;      // 👉 Import cho Query

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageStadiumActivity extends AppCompatActivity implements AdminStadiumAdapter.OnStadiumClickListener {

    private TextView txtManagementTitle, txtCurrentBranchName, txtCountTotal;
    private ImageView btnBackManagement, btnSearchInline;
    private LinearLayout btnFilterStatus, btnSortAZ;
    private RecyclerView recyclerViewManagementData;

    private List<Stadium> stadiumList;
    private AdminStadiumAdapter adapter;
    private DatabaseReference stadiumRef;
    private boolean isOwnerView = false;

    // 👉 DANH SÁCH ĐỆM: Giữ bản gốc dữ liệu từ Firebase để phục vụ bộ lọc
    private List<Stadium> fullStadiumList = new ArrayList<>();
    private boolean isAscending = true; // Cờ theo dõi trạng thái sắp xếp công thức phẳng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stadium);
        isOwnerView = getIntent().getBooleanExtra("isOwnerView", false);

        stadiumRef = FirebaseDatabase.getInstance().getReference("stadiums");
        stadiumList = new ArrayList<>();

        initViews();
        setupRecyclerView();

        btnBackManagement.setOnClickListener(v -> finish());

        // Nút kính lúp góc trên đảm nhận luồng mở màn hình thêm nhanh cụm sân lớn mới
        btnSearchInline.setOnClickListener(v ->
                startActivity(new Intent(ManageStadiumActivity.this, AddEditStadiumActivity.class))
        );

        // ==========================================
        // 🛠 1. XỬ LÝ SẮP XẾP CỤM SÂN THEO TÊN A -> Z
        // ==========================================
        btnSortAZ.setOnClickListener(v -> {
            Toast.makeText(this, isAscending ? "Đã sắp xếp cụm sân A -> Z" : "Đã sắp xếp cụm sân Z -> A", Toast.LENGTH_SHORT).show();
            sortStadiumsAlphabetically();
        });

        // ==========================================
        // 🛠 2. XỬ LÝ LỌC CỤM SÂN THEO TỈNH / THÀNH PHỐ
        // ==========================================
        btnFilterStatus.setOnClickListener(v -> {
            // Mảng danh sách các Tỉnh/Thành phố khớp với dữ liệu Admin hay nhập
            String[] cities = {"Tất cả tỉnh", "Vinh", "Nghệ An", "Bắc Giang"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chọn Tỉnh/Thành phố muốn lọc");
            builder.setItems(cities, (dialog, which) -> {
                List<Stadium> filteredList = new ArrayList<>();

                if (which == 0) { // Tất cả các tỉnh (nạp lại toàn bộ)
                    filteredList.addAll(fullStadiumList);
                } else {
                    String selectedCity = cities[which];
                    for (Stadium s : fullStadiumList) {
                        // So sánh trường City của model Stadium (không phân biệt hoa thường)
                        if (s.getCity() != null && s.getCity().equalsIgnoreCase(selectedCity)) {
                            filteredList.add(s);
                        }
                    }
                }

                // Cập nhật mảng hiển thị lên RecyclerView
                stadiumList.clear();
                stadiumList.addAll(filteredList);
                adapter.notifyDataSetChanged();

                // Cập nhật lại con số đếm huy chương Realtime đầu trang sau khi lọc
                txtCountTotal.setText(String.valueOf(stadiumList.size()));
            });
            builder.show();
        });

        loadStadiums();
    }

    private void initViews() {
        txtManagementTitle = findViewById(R.id.txtManagementTitle);
        txtCurrentBranchName = findViewById(R.id.txtCurrentBranchName);
        txtCountTotal = findViewById(R.id.txtCountTotal);
        btnBackManagement = findViewById(R.id.btnBackManagement);
        btnSearchInline = findViewById(R.id.btnSearchInline);
        btnFilterStatus = findViewById(R.id.btnFilterStatus);
        btnSortAZ = findViewById(R.id.btnSortAZ);
        recyclerViewManagementData = findViewById(R.id.recyclerViewManagementData);

        txtManagementTitle.setText("Quản lý cụm sân");
        txtCurrentBranchName.setText("Hệ thống tổng hợp chi nhánh");
    }

    private void setupRecyclerView() {
        recyclerViewManagementData.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminStadiumAdapter(stadiumList, this);
        recyclerViewManagementData.setAdapter(adapter);
    }

    private void loadStadiums() {
        // 1. Xác định UID hiện tại
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // 2. Tạo biến Query thay vì dùng trực tiếp Reference
        Query query;

        // 3. Nếu là chế độ xem của Owner (isOwnerView được truyền từ Intent)
        // Lưu ý: Khánh cần đảm bảo đã khai báo biến boolean isOwnerView ở đầu Activity
        // và nhận giá trị trong onCreate như mình đã gợi ý ở bước trước.
        if (isOwnerView) {
            // Chỉ lấy những sân có ownerId khớp với UID của chủ sân
            query = stadiumRef.orderByChild("ownerId").equalTo(myUid);
        } else {
            // Admin xem hết, không cần điều kiện
            query = stadiumRef;
        }

        // 4. Lắng nghe trên Query đã lọc
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stadiumList.clear();
                fullStadiumList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Stadium stadium = data.getValue(Stadium.class);
                    if (stadium != null) {
                        stadium.setId(data.getKey());
                        stadiumList.add(stadium);
                        fullStadiumList.add(stadium);
                    }
                }

                txtCountTotal.setText(String.valueOf(stadiumList.size()));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageStadiumActivity.this, "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // LOGIC THUẬT TOÁN XOAY VÒNG SẮP XẾP CHỮ CÁI
    // ==========================================
    private void sortStadiumsAlphabetically() {
        Collections.sort(stadiumList, (o1, o2) -> {
            if (isAscending) {
                return o1.getName().compareToIgnoreCase(o2.getName()); // A -> Z
            } else {
                return o2.getName().compareToIgnoreCase(o1.getName()); // Z -> A
            }
        });

        isAscending = !isAscending; // Đảo cờ
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Stadium stadium) {
        Intent intent = new Intent(ManageStadiumActivity.this, ManageCourtActivity.class);
        intent.putExtra("stadiumId", stadium.getId());
        intent.putExtra("stadiumName", stadium.getName());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Stadium stadium) {
        Intent intent = new Intent(ManageStadiumActivity.this, AddEditStadiumActivity.class);
        intent.putExtra("stadiumId", stadium.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(String stadiumId) {
        stadiumRef.child(stadiumId).removeValue()
                .addOnSuccessListener(unused -> Toast.makeText(this, "Xóa cụm sân thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Xóa thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}