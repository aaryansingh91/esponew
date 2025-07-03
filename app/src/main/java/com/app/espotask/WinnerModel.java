package com.app.espotask;

public class WinnerModel {
    String name, amount, date, imageUrl;

    public WinnerModel(String name, String amount, String date, String imageUrl) {
        this.name = name;
        this.amount = amount;
        this.date = date;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
