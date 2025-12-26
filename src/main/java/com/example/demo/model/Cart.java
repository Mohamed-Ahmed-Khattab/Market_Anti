package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private CartItem latestItem;
    private List<CartItem> items;
    private int cartID;
    private static int lastID = 0;
    private String status;
    private LocalDate creationDate;
    private double totalPrice;

    // Additional fields for DAO
    private int customerID;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Cart(int id, String status, double totalPrice) {
        this.cartID = id > 0 ? id : ++lastID;
        this.status = status;
        this.totalPrice = totalPrice;
        this.items = new ArrayList<>();
        this.creationDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
    }

    // Constructor matching UML somewhat or default
    public Cart() {
        this.cartID = ++lastID;
        this.items = new ArrayList<>();
        this.creationDate = LocalDate.now();
        this.createdAt = LocalDateTime.now();
        this.status = "New";
    }

    public void updateItemQuantity(Product p, int newQty) {
        for (CartItem item : items) {
            if (item.getProduct().equals(p)) {
                item.setQuantity(newQty);
                calculateTotalPrice();
                return;
            }
        }
    }

    public void removeItem(CartItem item) {
        if (items.remove(item)) {
            calculateTotalPrice();
        }
    }

    public void clear() {
        items.clear();
        totalPrice = 0;
        latestItem = null;
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void addItem(CartItem item) {
        if (item != null) {
            items.add(item);
            latestItem = item;
            calculateTotalPrice();
        }
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (CartItem item : items) {
            total += item.calculateTotalPrice();
        }
        this.totalPrice = total;
        return total;
    }

    // Getters and Setters matching DAO

    public void setLatestItem(CartItem latestItem) {
        this.latestItem = latestItem;
    }

    public CartItem getLatestItem() {
        return latestItem;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
        calculateTotalPrice();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}
