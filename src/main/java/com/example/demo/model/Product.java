package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Product implements SearchItem {
    private int productID;
    private String name;
    private String description;
    private String category;
    private int stockQuantity;
    private double price;
    private double cost;
    private int reorderLevel;
    private int supplierID;
    private String barcode;
    private String sku;
    private List<Product> substitue;

    public Product() {
        this.substitue = new ArrayList<>();
    }

    public Product(int productID, String name, int stockQuantity, double price) {
        this.productID = productID;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.substitue = new ArrayList<>();
        this.reorderLevel = 10; // Default
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

    public String getProductName() {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }
}
