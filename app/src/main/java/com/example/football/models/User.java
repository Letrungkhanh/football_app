package com.example.football.models;

public class User {
    public String uid;
    public String name;
    public String email;
    public String phone;
    public String avatar;
    public String role;
    public long createdAt;

    public User() {
        // Firebase cần constructor rỗng
    }

    public User(String uid, String name, String email, String phone,
                String avatar, String role, long createdAt) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatar = avatar;
        this.role = role;
        this.createdAt = createdAt;
    }
}
