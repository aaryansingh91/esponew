package com.aksofts.espotask;
public class HistoryItem {
    public static final int TYPE_COIN = 1;
    public static final int TYPE_TICKET = 2;

    private String date, description, creditDebit;
    private double amount;
    private int type;

    public HistoryItem(String date, String description, String creditDebit , double amount, int type) {
        this.date = date;
        this.description = description;
        this.creditDebit = creditDebit;
        this.amount = amount;
        this.type = type;
    }

    // Getters
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getCreditDebit() { return creditDebit; } // Updated
    public double getAmount() { return amount; }
    public int getType() { return type; }
}
