package com.example.football.activities.admin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageView; // Chèn dòng này vào cụm import ở đầu file

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.football.R;
import com.example.football.models.Stadium;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddEditStadiumActivity extends AppCompatActivity {

    private EditText edtStadiumName, edtStadiumAddress, edtStadiumCity, edtStadiumDistrict, edtStadiumImage;
    private Spinner spinnerStadiumCategory;
    private Button btnSaveStadium;
    private DatabaseReference stadiumRef;
    private String stadiumId = null;

    private final String[] categories = {"Bóng đá", "Cầu lông", "Pickleball", "Bóng chuyền"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_stadium);

        stadiumRef = FirebaseDatabase.getInstance().getReference("stadiums");
        initViews();
        setupCategorySpinner();

        if (getIntent() != null && getIntent().hasExtra("stadiumId")) {
            stadiumId = getIntent().getStringExtra("stadiumId");
            btnSaveStadium.setText("Cập nhật cụm sân");
            loadStadiumData();
        }

        btnSaveStadium.setOnClickListener(v -> saveStadium());
    }

    private void initViews() {
        edtStadiumName = findViewById(R.id.edtStadiumName);
        edtStadiumAddress = findViewById(R.id.edtStadiumAddress);
        edtStadiumCity = findViewById(R.id.edtStadiumCity);
        edtStadiumDistrict = findViewById(R.id.edtStadiumDistrict);
        edtStadiumImage = findViewById(R.id.edtStadiumImage);
        spinnerStadiumCategory = findViewById(R.id.spinnerStadiumCategory);
        btnSaveStadium = findViewById(R.id.btnSaveStadium);
        ImageView btnBackAddEdit = findViewById(R.id.btnBackAddEdit);
        btnBackAddEdit.setOnClickListener(v -> finish());
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStadiumCategory.setAdapter(adapter);
    }

    private void loadStadiumData() {
        if (stadiumId == null) return;
        stadiumRef.child(stadiumId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Stadium stadium = snapshot.getValue(Stadium.class);
                if (stadium != null) {
                    edtStadiumName.setText(stadium.getName());
                    edtStadiumAddress.setText(stadium.getAddress());
                    edtStadiumCity.setText(stadium.getCity());
                    edtStadiumDistrict.setText(stadium.getDistrict());
                    edtStadiumImage.setText(stadium.getImage());

                    if (stadium.getCategory() != null) {
                        for (int i = 0; i < categories.length; i++) {
                            if (categories[i].equals(stadium.getCategory())) {
                                spinnerStadiumCategory.setSelection(i);
                                break;
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void saveStadium() {
        String name = edtStadiumName.getText().toString().trim();
        String address = edtStadiumAddress.getText().toString().trim();
        String city = edtStadiumCity.getText().toString().trim();
        String district = edtStadiumDistrict.getText().toString().trim();
        String image = edtStadiumImage.getText().toString().trim();
        String selectedCategory = spinnerStadiumCategory.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(city)) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (stadiumId == null) {
            stadiumId = stadiumRef.push().getKey();
        }

        String ownerId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : "admin";

        Stadium stadium = new Stadium(stadiumId, name, address, city, district, image, ownerId, selectedCategory);

        if (stadiumId != null) {
            stadiumRef.child(stadiumId).setValue(stadium)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(AddEditStadiumActivity.this, "Lưu cụm sân thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(AddEditStadiumActivity.this, "Lưu thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}