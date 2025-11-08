package com.rewards.espotask;

public class SliderModel {
    private String id;
    private String imageUrl;
    private String clickUrl;
    private String status;
    private String createdAt;

    public SliderModel(String id, String imageUrl, String clickUrl, String status, String createdAt) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.clickUrl = clickUrl;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getClickUrl() {
        return clickUrl;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean hasClickUrl() {
        return clickUrl != null && !clickUrl.isEmpty() && !clickUrl.equals("null");
    }
}