package com.aksofts.mgamerapp;
public class HistoryItem {
    public static final int TYPE_TICKET = 0;
    public static final int TYPE_COIN = 1;

    private String date;
    private String description;
    private String transactionId;
    private int amount;
    private int type; // 0 for ticket, 1 for coin

    public HistoryItem(String date, String description, String transactionId, int amount, int type) {
        this.date = date;
        this.description = description;
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getAmount() {
        return amount;
    }

    public int getType() {
        return type;
    }
}
