package com.example.football.models;

public class Stadium {
    private String id;
    private String name;
    private String address;
    private String city;
    private String district;
    private String image;
    private String ownerId;
    private String category;

    // Constructor rỗng bắt buộc cho Firebase Realtime Database khi ép kiểu data.getValue(Stadium.class)
    public Stadium() {}

    public Stadium(String id, String name, String address, String city,
                   String district, String image, String ownerId,String category) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.city = city;
        this.district = district;
        this.image = image;
        this.ownerId = ownerId;
        this.category = category;
    }

    // --- GETTER METHODS ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getDistrict() { return district; }
    public String getImage() { return image; }
    public String getOwnerId() { return ownerId; }

    // --- SETTER METHODS (Bổ sung để hết lỗi gạch đỏ ở MainActivity) ---
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}