package com.example.football.activities.user;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.football.R;
import com.example.football.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "KIEM_TRA_AUTH";

    private EditText emailEditText, passwordEditText, nameEditText, phoneEditText;
    private Button registerButton;

    private FirebaseAuth mAuth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");

        // Ánh xạ view
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(v -> {
            Log.d(TAG, "Đã nhấn nút Đăng ký!");
            Toast.makeText(this, "Đang xử lý đăng ký...", Toast.LENGTH_SHORT).show();

            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String name = nameEditText.getText().toString().trim();
            String phone = phoneEditText.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Thiếu thông tin đăng ký");
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải từ 6 ký tự", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Mật khẩu quá ngắn");
                return;
            }

            Log.d(TAG, "Email: " + email + " | Name: " + name + " | Phone: " + phone);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Tạo tài khoản Firebase Auth thành công");

                            if (mAuth.getCurrentUser() == null) {
                                Log.e(TAG, "mAuth.getCurrentUser() bị null");
                                Toast.makeText(this, "Lỗi: Không lấy được người dùng", Toast.LENGTH_LONG).show();
                                return;
                            }

                            String uid = mAuth.getCurrentUser().getUid();

                            User user = new User(
                                    uid,
                                    name,
                                    email,
                                    phone,
                                    "",
                                    "user",
                                    System.currentTimeMillis()
                            );

                            database.child(uid).setValue(user)
                                    .addOnSuccessListener(unused -> {
                                        Log.d(TAG, "Lưu user vào Realtime Database thành công");
                                        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Lỗi lưu Database: " + e.getMessage());
                                        Toast.makeText(this, "Lỗi lưu dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    });

                        } else {
                            String error = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Lỗi không xác định";

                            Log.e(TAG, "Đăng ký thất bại: " + error);
                            Toast.makeText(this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}