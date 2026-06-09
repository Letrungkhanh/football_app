package com.example.football.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.football.activities.user.LoginActivity;

import com.example.football.R;
import com.example.football.adapters.UserStadiumAdapter;
import com.example.football.models.Stadium;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query; // ĐÃ THÊM: Để thực hiện truy vấn lọc Firebase
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserStadiumAdapter.OnStadiumClickListener {

    private RecyclerView recyclerViewUserStadiums;
    private List<Stadium> stadiumList;
    private UserStadiumAdapter adapter;
    private DatabaseReference stadiumRef;

    private TextView txtHeaderDate;
    private Button btnHomeLogin, btnHomeRegister;
    private EditText edtHomeSearch;
    private ImageView btnFavoriteList;
    private ImageView imgUserProfile;
    private LinearLayout catFootball, catBadminton, catPickleball, catVolleyball;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stadiumRef = FirebaseDatabase.getInstance().getReference("stadiums");
        stadiumList = new ArrayList<>();

        initViews();

        // 👉 MẶC ĐỊNH: Khi mới mở App, tải toàn bộ cụm sân không phân biệt danh mục
        loadStadiumsData();

        setupClickListeners();
    }

    private void initViews() {
        recyclerViewUserStadiums = findViewById(R.id.recyclerViewUserStadiums);
        recyclerViewUserStadiums.setLayoutManager(new LinearLayoutManager(this));

        adapter = new UserStadiumAdapter(stadiumList, this);
        recyclerViewUserStadiums.setAdapter(adapter);

        txtHeaderDate = findViewById(R.id.txtHeaderDate);
        btnHomeLogin = findViewById(R.id.btnHomeLogin);
        btnHomeRegister = findViewById(R.id.btnHomeRegister);
        edtHomeSearch = findViewById(R.id.edtHomeSearch);
        btnFavoriteList = findViewById(R.id.btnFavoriteList);

        catFootball = findViewById(R.id.catFootball);
        catBadminton = findViewById(R.id.catBadminton);
        catPickleball = findViewById(R.id.catPickleball);
        catVolleyball = findViewById(R.id.catVolleyball);
        imgUserProfile = findViewById(R.id.imgUserProfile);
    }

    private void setupClickListeners() {
        btnHomeLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });
        imgUserProfile.setOnClickListener(v -> {
            // Khi bấm vào hình User ở góc phải màn hình, nhảy thẳng sang trang cá nhân ProfileActivity của bạn
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
        btnHomeRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // =========================================================================
        // 👉 ĐÃ CẬP NHẬT: Gọi hàm lọc dữ liệu từ Firebase theo từng nút danh mục bấm
        // =========================================================================
        catFootball.setOnClickListener(v -> {
            loadStadiumsByCategory("Bóng đá");
            Toast.makeText(this, "⚽ Đang hiển thị cụm sân Bóng đá", Toast.LENGTH_SHORT).show();
        });

        catBadminton.setOnClickListener(v -> {
            loadStadiumsByCategory("Cầu lông");
            Toast.makeText(this, "🏸 Đang hiển thị cụm sân Cầu lông", Toast.LENGTH_SHORT).show();
        });

        catPickleball.setOnClickListener(v -> {
            loadStadiumsByCategory("Pickleball");
            Toast.makeText(this, "🏓 Đang hiển thị cụm sân Pickleball", Toast.LENGTH_SHORT).show();
        });

        catVolleyball.setOnClickListener(v -> {
            loadStadiumsByCategory("Bóng chuyền");
            Toast.makeText(this, "🏐 Đang hiển thị cụm sân Bóng chuyền", Toast.LENGTH_SHORT).show();
        });
    }

    // Hàm lấy TOÀN BỘ danh sách sân (Giữ nguyên logic ban đầu của bạn)
    private void loadStadiumsData() {
        stadiumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stadiumList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Stadium stadium = data.getValue(Stadium.class);
                    if (stadium != null) {
                        stadium.setId(data.getKey());
                        stadiumList.add(stadium);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi kết nối Firebase: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =========================================================================
    // 👉 ĐÃ THÊM MỚI: Hàm truy vấn động thực hiện lọc Category từ Realtime Database
    // =========================================================================
    private void loadStadiumsByCategory(String categoryName) {
        // Tạo câu lệnh Query lọc theo trường "category" khớp với tên danh mục chọn
        Query filterQuery = stadiumRef.orderByChild("category").equalTo(categoryName);

        filterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stadiumList.clear(); // Xóa sạch các cụm sân của danh mục trước đó

                for (DataSnapshot data : snapshot.getChildren()) {
                    Stadium stadium = data.getValue(Stadium.class);
                    if (stadium != null) {
                        stadium.setId(data.getKey());
                        stadiumList.add(stadium); // Thêm các sân đúng thể loại vào danh sách
                    }
                }

                adapter.notifyDataSetChanged(); // Làm mới lại giao diện hiển thị công tâm

                // Nếu danh mục trống, thông báo nhanh cho khách biết
                if (stadiumList.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Hiện tại chưa có cụm sân " + categoryName + " nào!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi bộ lọc: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStadiumClick(Stadium stadium) {
        Intent intent = new Intent(MainActivity.this, UserSelectCourtActivity.class);
        intent.putExtra("stadiumId", stadium.getId());
        intent.putExtra("stadiumName", stadium.getName());
        startActivity(intent);
    }
}