package com.example.football.activities.admin;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Thư viện cứu cánh cho Dialog lọc
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.football.R;
import com.example.football.adapters.UserAdapter;
import com.example.football.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity implements UserAdapter.OnUserClickListener {

    private TextView txtManagementTitle, txtCountBronze, txtCountGold, txtCountTotal;
    private ImageView btnBackManagement, btnSearchInline;
    private LinearLayout btnFilterStatus, btnSortAZ;
    private RecyclerView recyclerViewManagementData;

    private List<User> userList;
    private UserAdapter adapter;
    private DatabaseReference userRef;

    // 👉 DANH SÁCH ĐỆM: Giữ bản gốc danh sách khách hàng bốc từ Firebase về để lọc
    private List<User> fullUserList = new ArrayList<>();
    private boolean isAscending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        userRef = FirebaseDatabase.getInstance().getReference("users");
        userList = new ArrayList<>();

        initViews();
        setupRecyclerView();

        btnBackManagement.setOnClickListener(v -> finish());

        // ==========================================
        // 🛠 1. XỬ LÝ SẮP XẾP TÊN KHÁCH HÀNG A -> Z
        // ==========================================
        btnSortAZ.setOnClickListener(v -> {
            Toast.makeText(this, isAscending ? "Đã sắp xếp khách hàng A -> Z" : "Đã sắp xếp khách hàng Z -> A", Toast.LENGTH_SHORT).show();
            sortUsersAlphabetically();
        });

        // ==========================================
        // 🛠 2. XỬ LÝ LỌC NHANH THEO CẤP BẬC PHÂN QUYỀN
        // ==========================================
        btnFilterStatus.setOnClickListener(v -> {
            String[] roles = {"Tất cả quyền", "Quản trị viên (Admin)", "Thành viên (User)"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Chọn phân quyền muốn lọc");
            builder.setItems(roles, (dialog, which) -> {
                List<User> filteredList = new ArrayList<>();

                if (which == 0) { // Tất cả quyền tài khoản
                    filteredList.addAll(fullUserList);
                } else if (which == 1) { // Chỉ lọc lấy Admin
                    for (User u : fullUserList) {
                        if ("admin".equalsIgnoreCase(u.getRole())) {
                            filteredList.add(u);
                        }
                    }
                } else if (which == 2) { // Chỉ lọc lấy User thường
                    for (User u : fullUserList) {
                        if (!"admin".equalsIgnoreCase(u.getRole())) {
                            filteredList.add(u);
                        }
                    }
                }

                // Cập nhật mảng và vẽ lại giao diện bảng phẳng
                userList.clear();
                userList.addAll(filteredList);
                adapter.notifyDataSetChanged();

                // Cập nhật lại tổng số tài khoản hiển thị sau khi lọc trên huy chương xanh lá
                txtCountTotal.setText(String.valueOf(userList.size()));
            });
            builder.show();
        });

        // ==========================================
        // 🛠 3. NÚT KÍNH LÚP: HỖ TRỢ CHỨC NĂNG TÌM KIẾM THEO TÊN
        // ==========================================
        btnSearchInline.setOnClickListener(v -> {
            // Tạo nhanh một ô nhập văn bản (EditText) ngay trên Dialog để Admin gõ tên
            final android.widget.EditText input = new android.widget.EditText(this);
            input.setHint("Nhập tên khách hàng cần tìm...");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Tìm kiếm nhanh");
            builder.setView(input);
            builder.setPositiveButton("Tìm kiếm", (dialog, which) -> {
                String searchName = input.getText().toString().trim();
                filterSearchUsers(searchName);
            });
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        loadAllUsers();
    }

    private void initViews() {
        txtManagementTitle = findViewById(R.id.txtManagementTitle);
        txtCountBronze = findViewById(R.id.txtCountBronze);
        txtCountGold = findViewById(R.id.txtCountGold);
        txtCountTotal = findViewById(R.id.txtCountTotal);
        btnBackManagement = findViewById(R.id.btnBackManagement);
        btnSearchInline = findViewById(R.id.btnSearchInline);
        btnFilterStatus = findViewById(R.id.btnFilterStatus);
        btnSortAZ = findViewById(R.id.btnSortAZ);
        recyclerViewManagementData = findViewById(R.id.recyclerViewManagementData);

        txtManagementTitle.setText("Danh sách khách hàng");
    }

    private void setupRecyclerView() {
        recyclerViewManagementData.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserAdapter(userList, this);
        recyclerViewManagementData.setAdapter(adapter);
    }

    private void loadAllUsers() {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                fullUserList.clear(); // Dọn sạch mảng gốc backup
                int countUser = 0;
                int countAdmin = 0;

                for (DataSnapshot data : snapshot.getChildren()) {
                    User user = data.getValue(User.class);
                    if (user != null) {
                        user.setUid(data.getKey());

                        // Đồng bộ đổ vào cả 2 mảng danh sách
                        userList.add(user);
                        fullUserList.add(user);

                        if ("admin".equalsIgnoreCase(user.getRole())) {
                            countAdmin++;
                        } else {
                            countUser++;
                        }
                    }
                }

                txtCountBronze.setText(String.valueOf(countUser));
                txtCountGold.setText(String.valueOf(countAdmin));
                txtCountTotal.setText(String.valueOf(userList.size()));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageUsersActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // THUẬT TOÁN TÌM KIẾM THEO CHUỖI KÝ TỰ (SEARCH)
    // ==========================================
    private void filterSearchUsers(String text) {
        List<User> filteredList = new ArrayList<>();
        for (User item : fullUserList) {
            // Kiểm tra xem tên khách hàng có chứa từ khóa gõ vào không (không phân biệt hoa thường)
            if (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        userList.clear();
        userList.addAll(filteredList);
        adapter.notifyDataSetChanged();
        txtCountTotal.setText(String.valueOf(userList.size()));
    }

    // ==========================================
    // THUẬT TOÁN SẮP XẾP CHỮ CÁI DỰA TRÊN SO SÁNH CHUỖI
    // ==========================================
    private void sortUsersAlphabetically() {
        Collections.sort(userList, (o1, o2) -> {
            if (isAscending) {
                return o1.getName().compareToIgnoreCase(o2.getName()); // Tên từ A -> Z
            } else {
                return o2.getName().compareToIgnoreCase(o1.getName()); // Tên từ Z -> A
            }
        });

        isAscending = !isAscending; // Hoán đổi trạng thái cờ
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onToggleRoleClick(User user) {
        String newRole = "admin".equalsIgnoreCase(user.getRole()) ? "user" : "admin";

        userRef.child(user.getUid()).child("role").setValue(newRole)
                .addOnSuccessListener(unused -> Toast.makeText(this, "Đã cập nhật quyền thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}