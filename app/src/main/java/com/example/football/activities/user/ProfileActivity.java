package com.example.football.activities.user;

import android.content.Intent; // Thêm import Intent để chuyển màn hình
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.football.R;
import com.example.football.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private TextView txtEmail;
    private EditText edtName, edtPhone;
    private Button btnSave, btnHistory,btnLogout; // Thêm biến btnHistory ở đây
    private DatabaseReference userRef;
    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUserId);

        initViews();

        loadCurrentUserData();

        // Sự kiện lưu thông tin cũ của bạn
        btnSave.setOnClickListener(v -> updateProfileData());

        btnLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(ProfileActivity.this,"Đã đăng xuất thành công",Toast.LENGTH_SHORT).show();
            finish();
        });

        // ĐÃ THÊM: Bắt sự kiện click để nhảy sang màn hình Lịch sử đặt sân
        btnHistory.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, BookingHistoryActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        txtEmail = findViewById(R.id.txtProfileEmail);
        edtName = findViewById(R.id.edtProfileName);
        edtPhone = findViewById(R.id.edtProfilePhone);
        btnSave = findViewById(R.id.btnSaveProfile);
        btnHistory = findViewById(R.id.btnBookingHistory);
        btnLogout = findViewById(R.id.btnProfileLogout);

    }


    private void loadCurrentUserData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        txtEmail.setText(user.getEmail());
                        edtName.setText(user.getName());
                        edtPhone.setText(user.getPhone());
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Không tìm thấy dữ liệu của UID này trên Database!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải thông tin!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfileData() {
        String newName = edtName.getText().toString().trim();
        String newPhone = edtPhone.getText().toString().trim();

        if (TextUtils.isEmpty(newName)) {
            Toast.makeText(this, "Họ và tên không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(newPhone)) {
            Toast.makeText(this, "Số điện thoại không được để trống!", Toast.LENGTH_SHORT).show();
            return;
        }

        userRef.child("name").setValue(newName);
        userRef.child("phone").setValue(newPhone)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}