package com.example.football.models;

public class Field {
    private String id;
    private String name;
    private String city;
    private String district;
    private String address;
    private int price;
    private String image;
    private String status;

    public Field() {
        // Firebase cần constructor rỗng
    }

    public Field(String id, String name, String city, String district, String address, int price, String image, String status) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.district = district;
        this.address = address;
        this.price = price;
        this.image = image;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getDistrict() {
        return district;
    }

    public String getAddress() {
        return address;
    }

    public int getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}