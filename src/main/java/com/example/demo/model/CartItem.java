package com.example.demo.model;

import java.time.LocalDate;

public class CartItem {
    private Product product;
    private final int cartItemID; // readOnly
    private static int lastID = 0;
    private int quantity;
    private double unitPrice;
    private LocalDate expireDate;

    public CartItem(LocalDate expireDate, double unitPrice, int quantity, Product product) {
        this.cartItemID = ++lastID;
        this.expireDate = expireDate;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.product = product;
    }

    @Override
    public String toString() {
        return "CartItem{id=" + cartItemID + ", product=" + (product != null ? product.getName() : "null") + "}";
    }

    public void increaseQuantity(int amount) {
        if (amount > 0)
            this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount > 0 && this.quantity >= amount)
            this.quantity -= amount;
    }

    public double calculateTotalPrice() {
        return quantity * unitPrice;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Product getProduct() {
        return product;
    }

    public int getCartItemID() {
        return cartItemID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }
}
