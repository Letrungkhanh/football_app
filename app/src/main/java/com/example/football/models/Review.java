package com.example.football.models;

public class Review {
    private String reviewId;
    private String stadiumId;
    private String userId;
    private String userName;
    private float rating; // Dùng float để khớp với RatingBar (ví dụ 4.5 sao)
    private String comment;
    private long timestamp;
    private boolean visible = true;
    public Review() {} // Bắt buộc cho Firebase

    public Review(String reviewId, String stadiumId, String userId, String userName, float rating, String comment, long timestamp) {
        this.reviewId = reviewId;
        this.stadiumId = stadiumId;
        this.userId = userId;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.timestamp = timestamp;
        this.visible = true;
    }

    // Getter và Setter
    public String getReviewId() { return reviewId; }
    public String getStadiumId() { return stadiumId; }
    public String getUserId() { return userId; }
    public String getUserName() { return userName; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public long getTimestamp() { return timestamp; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
}