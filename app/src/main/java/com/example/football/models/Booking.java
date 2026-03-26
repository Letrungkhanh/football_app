package com.example.football.models;

public class Booking {
    private String id;
    private String userId;
    private String fieldId;
    private String fieldName;
    private String date;
    private String time;
    private int totalPrice;
    private String status;

    public Booking() {
        // Firebase cần constructor rỗng
    }

    public Booking(String id, String userId, String fieldId, String fieldName, String date, String time, int totalPrice, String status) {
        this.id = id;
        this.userId = userId;
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.date = date;
        this.time = time;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFieldId() {
        return fieldId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}