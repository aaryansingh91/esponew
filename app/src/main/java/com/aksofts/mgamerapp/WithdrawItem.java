package com.aksofts.mgamerapp;

public class WithdrawItem {
    private String id;
    private String name;
    private String type;
    private String coins_amount;
    private String tickets_amount;
    private String reward_amount;
    private String category;
    private String status;

    // Constructor
    public WithdrawItem(String id, String name, String type, String coins_amount, String reward_amount, String category, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.coins_amount = coins_amount;
        this.tickets_amount = tickets_amount;
        this.reward_amount = reward_amount;
        this.category = category;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCoins_amount() {
        return coins_amount;
    }

    public String getTickets_amount() {
        return tickets_amount;
    }

    public String getReward_amount() {
        return reward_amount;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }
}
