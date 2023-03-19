package com.example.babybuy.Models;

public class Item {
    String name, imageURL, price, description, delegateNumber, userID, purchased, itemID;

    public Item(String name, String imageURL, String price, String description, String delegateNumber, String userID, String purchased) {
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;
        this.description = description;
        this.delegateNumber = delegateNumber;
        this.userID = userID;
        this.purchased = purchased;
    }

    public Item(String name, String imageURL, String price, String description, String delegateNumber, String userID, String purchased, String itemID) {
        this.name = name;
        this.imageURL = imageURL;
        this.price = price;
        this.description = description;
        this.delegateNumber = delegateNumber;
        this.userID = userID;
        this.purchased = purchased;
        this.itemID = itemID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getPurchased() {
        return purchased;
    }

    public void setPurchased(String purchased) {
        this.purchased = purchased;
    }

    public String getDelegateNumber() {
        return delegateNumber;
    }

    public void setDelegateNumber(String delegateNumber) {
        this.delegateNumber = delegateNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
