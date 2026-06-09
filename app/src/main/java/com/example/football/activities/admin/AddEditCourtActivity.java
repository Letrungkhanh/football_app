package com.example.football.activities.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.football.R;
import com.example.football.models.Court;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddEditCourtActivity extends AppCompatActivity {

    private EditText edtCourtName, edtCourtPrice, edtStadiumId, edtCourtType, edtCourtStatus;
    private Button btnSaveCourt;

    private DatabaseReference courtRef;
    private String courtId = null;
    private String stadiumId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_court);

        courtRef = FirebaseDatabase.getInstance().getReference("courts");

        initViews();

        if (getIntent() != null) {
            stadiumId = getIntent().getStringExtra("stadiumId");

            // Nếu bốc từ trang tổng chưa có stadiumId, mở quyền cho Admin tự điền
            if (stadiumId != null && !stadiumId.trim().isEmpty()) {
                edtStadiumId.setText(stadiumId);
                edtStadiumId.setEnabled(false); // Khóa lại chống phá liên kết cụm sân
            } else {
                edtStadiumId.setEnabled(true);
            }

            if (getIntent().hasExtra("courtId")) {
                courtId = getIntent().getStringExtra("courtId");
                btnSaveCourt.setText("Cập nhật thông tin sân");
                loadCurrentCourtData();
            }
        }

        btnSaveCourt.setOnClickListener(v -> saveCourtToFirebase());
    }

    private void initViews() {
        edtCourtName = findViewById(R.id.edtCourtName);
        edtCourtPrice = findViewById(R.id.edtCourtPrice);
        edtStadiumId = findViewById(R.id.edtStadiumId);
        edtCourtType = findViewById(R.id.edtCourtType);
        edtCourtStatus = findViewById(R.id.edtCourtStatus);
        btnSaveCourt = findViewById(R.id.btnSaveCourt);
        ImageView btnBackAddEditCourt = findViewById(R.id.btnBackAddEditCourt);
        if (btnBackAddEditCourt != null) {
            btnBackAddEditCourt.setOnClickListener(v -> finish());
        }
    }

    private void loadCurrentCourtData() {
        if (courtId == null) return;
        courtRef.child(courtId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Court court = snapshot.getValue(Court.class);
                if (court != null) {
                    edtCourtName.setText(court.getName());
                    edtCourtPrice.setText(String.valueOf(court.getPrice()));
                    edtCourtType.setText(court.getType());
                    edtCourtStatus.setText(court.getStatus());
                    if (court.getStadiumId() != null) {
                        edtStadiumId.setText(court.getStadiumId());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveCourtToFirebase() {
        String name = edtCourtName.getText().toString().trim();
        String priceStr = edtCourtPrice.getText().toString().trim();
        String type = edtCourtType.getText().toString().trim();
        String status = edtCourtStatus.getText().toString().trim();
        String finalStadiumId = edtStadiumId.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(priceStr) || TextUtils.isEmpty(finalStadiumId)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ các thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);

        if (courtId == null) {
            courtId = courtRef.push().getKey();
        }

        // Tạo Object Court đóng gói sạch sẽ
        Court court = new Court(courtId, finalStadiumId, name, type, price, status);

        if (courtId != null) {
            courtRef.child(courtId).setValue(court)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(AddEditCourtActivity.this, "Lưu thông tin sân nhỏ thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddEditCourtActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}