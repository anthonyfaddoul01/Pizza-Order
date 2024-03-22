package com.example.navigationbarapp;

// MenuItem.java
public class MenuItem {
    private String itemName;
    private String itemPrice;
    private int imageResourceId;

    public MenuItem(String itemName, String itemPrice, int imageResourceId) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.imageResourceId = imageResourceId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}
