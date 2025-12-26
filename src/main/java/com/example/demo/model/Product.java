package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Product implements SearchItem {
    private int productID;
    private String name;
    private int stockQuantity;
    private double price;
    private List<Product> substitue;

    public Product(int productID, String name, int stockQuantity, double price) {
        this.productID = productID;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.substitue = new ArrayList<>();
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null)
            return false;
        return name.toLowerCase().contains(keyword.toLowerCase());
    }

    public String getStatusColor() {
        if (stockQuantity <= 0)
            return "RED";
        if (stockQuantity < 5)
            return "YELLOW";
        return "GREEN";
    }

    public boolean isOutOfStock() {
        return stockQuantity <= 0;
    }

    public void updateStock(int qty) {
        this.stockQuantity = qty;
    }

    public boolean reduceStock(int quantity) {
        if (stockQuantity >= quantity) {
            stockQuantity -= quantity;
            return true;
        }
        return false;
    }

    public void addSubstitute(Product p) {
        if (p != null && !substitue.contains(p)) {
            substitue.add(p);
        }
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getProductID() {
        return productID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public void setSubstitue(List<Product> substitue) {
        this.substitue = substitue;
    }

    public List<Product> getSubstitue() {
        return substitue;
    }
}
