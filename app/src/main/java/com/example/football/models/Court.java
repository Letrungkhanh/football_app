package com.example.football.models;

public class Court {
    private String id;
    private String stadiumId;
    private String name;
    private String type;
    private int price;
    private String status;

    public Court() {}

    public Court(String id, String stadiumId, String name,
                 String type, int price, String status) {
        this.id = id;
        this.stadiumId = stadiumId;
        this.name = name;
        this.type = type;
        this.price = price;
        this.status = status;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() { return id; }
    public String getStadiumId() { return stadiumId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getPrice() { return price; }
    public String getStatus() { return status; }

}