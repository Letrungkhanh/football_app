package com.example.football.activities.user;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.football.activities.user.MainActivity;

import com.example.football.R;
import com.example.football.activities.admin.AdminActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.football.activities.owner.OwnerActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView goToRegisterTextView;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        // Luồng tự động đăng nhập nếu session cũ còn giữ
        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid(); // Lấy UID chuẩn của tài khoản
            checkUserRole(uid);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("users");

        initViews();

        if (loginButton != null) {
            loginButton.setOnClickListener(v -> handleLogin());
        }

        if (goToRegisterTextView != null) {
            goToRegisterTextView.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish();
            });
        }
    }

    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        goToRegisterTextView = findViewById(R.id.goToRegisterTextView);
    }

    private void handleLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            emailEditText.setError("Vui lòng nhập Email");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Vui lòng nhập mật khẩu");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser() != null) {
                            String uid = mAuth.getCurrentUser().getUid(); // Đăng nhập xong, lấy UID thực tế
                            checkUserRole(uid);
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Sai tài khoản hoặc mật khẩu!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    // ĐỒNG BỘ CHUẨN: Kiểm tra role bằng cách tìm theo UID của tài khoản
    private void checkUserRole(String uid) {
        userRef.child(uid).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);

                if (role == null) role = "user"; // Mặc định là user nếu chưa có role

                if (role.equalsIgnoreCase("admin")) {
                    Toast.makeText(LoginActivity.this, "Chào Admin!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                }
                else if (role.equalsIgnoreCase("owner")) {
                    // 👉 KHÁNH CẦN TẠO ACTIVITY NÀY: Dành riêng cho Chủ sân
                    Toast.makeText(LoginActivity.this, "Chào Chủ sân!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, OwnerActivity.class));
                }
                else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}