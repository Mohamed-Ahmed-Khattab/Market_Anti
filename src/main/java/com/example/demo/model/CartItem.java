package com.example.demo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CartItem {
    private Product product;
    private int cartItemID; // To match DAO ID handling
    private static int lastID = 0;
    private int quantity;
    private double unitPrice;
    private LocalDate expireDate;

    // Additional fields from DAO usages
    private int cartID;
    private int productID;
    private double priceAtAdd;
    private LocalDateTime addedAt;

    public CartItem() {
        this.cartItemID = ++lastID;
        this.addedAt = LocalDateTime.now();
    }

    public CartItem(LocalDate expireDate, double unitPrice, int quantity, Product product) {
        this.cartItemID = ++lastID;
        this.expireDate = expireDate;
        this.unitPrice = unitPrice;
        this.priceAtAdd = unitPrice; // default
        this.quantity = quantity;
        this.product = product;
        if (product != null) {
            this.productID = product.getProductID();
        }
        this.addedAt = LocalDateTime.now();
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

    // Getters and Setters matching DAO and Controller usage

    public void setProduct(Product product) {
        this.product = product;
        if (product != null) {
            this.productID = product.getProductID();
        }
    }

    public Product getProduct() {
        return product;
    }

    public int getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(int cartItemID) {
        this.cartItemID = cartItemID;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
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

    public double getPriceAtAdd() {
        return priceAtAdd;
    }

    public void setPriceAtAdd(double priceAtAdd) {
        this.priceAtAdd = priceAtAdd;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }
}
