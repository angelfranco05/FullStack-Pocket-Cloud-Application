package com.example.myapp;

public class InventoryItem {

    String userId;
    String inventoryName;
    String inventoryQty;
    String inventoryPrice;


    public InventoryItem() {

    }

    public InventoryItem(String userId, String inventoryName, String inventoryQty, String inventoryPrice) {
        this.userId = userId;
        this.inventoryName = inventoryName;
        this.inventoryQty = inventoryQty;
        this.inventoryPrice = inventoryPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

    public String getInventoryQty() {
        return inventoryQty;
    }

    public void setInventoryQty(String inventoryQty) {
        this.inventoryQty = inventoryQty;
    }

    public String getInventoryPrice() {
        return inventoryPrice;
    }

    public void setInventoryPrice(String inventoryPrice) {
        this.inventoryPrice = inventoryPrice;
    }
}
