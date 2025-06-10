package com.app.espotask;


public class WithdrawSelectionItem {
    private String id;
    private String abbrevation;
    private String title;
    private String description;
    private String img_icon;

    // Constructor
    public WithdrawSelectionItem(String id, String abbrevation, String title, String description, String img_icon) {
        this.id = id;
        this.abbrevation = abbrevation;
        this.title = title;
        this.description = description;
        this.img_icon = img_icon;
    }

    // Getters
    public String getId() { return id; }
    public String getAbbrevation() { return abbrevation; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImgIcon() { return img_icon; }
}
