package com.example.football;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookingActivity extends AppCompatActivity {

    private TextView fieldNameTextView, fieldPriceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        fieldNameTextView = findViewById(R.id.fieldNameTextView);
        fieldPriceTextView = findViewById(R.id.fieldPriceTextView);

        // Nhận dữ liệu từ Intent
        String fieldName = getIntent().getStringExtra("fieldName");
        int fieldPrice = getIntent().getIntExtra("fieldPrice", 0);

        fieldNameTextView.setText("Tên sân: " + fieldName);
        fieldPriceTextView.setText("Giá: " + fieldPrice + " VNĐ");
    }
}