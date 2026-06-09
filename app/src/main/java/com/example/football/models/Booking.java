package com.example.football.models;

public class Booking {
    private String id;
    private String courtId;
    private String courtName;
    private String userId;
    private String userPhone; // Đảm bảo có thuộc tính này
    private String date;
    private String slotTime;  // Đảm bảo có thuộc tính này
    private int totalPrice;
    private String status;
    private long createdAt;
    private String selectedServices;
    private String stadiumId;

    // Constructor rỗng bắt buộc cho Firebase
    public Booking() {
    }

    // Constructor đầy đủ
    public Booking(String id, String courtId, String courtName, String userId, String userPhone, String date, String slotTime, int totalPrice, String status, long createdAt) {
        this.id = id;
        this.courtId = courtId;
        this.courtName = courtName;
        this.userId = userId;
        this.userPhone = userPhone;
        this.date = date;
        this.slotTime = slotTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    // --- CÁC HÀM GETTER / SETTER QUAN TRỌNG ĐỂ HẾT LỖI ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourtId() { return courtId; }
    public void setCourtId(String courtId) { this.courtId = courtId; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserPhone() { return userPhone; }
    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getSlotTime() { return slotTime; }
    public void setSlotTime(String slotTime) { this.slotTime = slotTime; }

    public int getTotalPrice() { return totalPrice; }
    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public String getSelectedServices() { return selectedServices; }
    public void setSelectedServices(String selectedServices) { this.selectedServices = selectedServices; }
    // 2. Thêm Getter
    public String getStadiumId() {
        return stadiumId;
    }

    // 3. Thêm Setter
    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }
}