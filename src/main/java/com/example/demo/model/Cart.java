package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cart entity representing shopping carts
 * Maps to the Cart table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Integer cartID;
    private Integer customerID;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String status; // active, checked_out, abandoned

    // Associations
    private java.util.List<CartItem> items = new java.util.ArrayList<>();
    private CartItem latestItem;

    public void addItem(CartItem item) {
        items.add(item);
        latestItem = item;
    }

    public void removeItem(CartItem item) {
        items.remove(item);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double calculateTotal() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();
    }

    public void checkout() {
        this.status = "checked_out";
    }

    // Getters for lists
    public java.util.List<CartItem> getItems() {
        return items;
    }

    public void setItems(java.util.List<CartItem> items) {
        this.items = items;
    }

    public CartItem getLatestItem() {
        return latestItem;
    }

    public void setLatestItem(CartItem latestItem) {
        this.latestItem = latestItem;
    }

    // Constructor without ID for new carts
    public Cart(Integer customerID, String status) {
        this.customerID = customerID;
        this.status = status;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if cart is active
     */
    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }
}
