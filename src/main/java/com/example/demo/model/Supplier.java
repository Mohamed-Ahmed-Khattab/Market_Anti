package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private final int supplierID; // readOnly
    private static int lastID = 0;
    private List<Product> products;
    private String name;
    private double rating;
    private boolean active;

    public Supplier(String name, double rating) {
        this.supplierID = ++lastID;
        this.name = name;
        this.rating = rating;
        this.active = true;
        this.products = new ArrayList<>();
    }

    public void supplyProduct(Product p, int quantity) {
        if (active && p != null) {
            p.updateStock(p.getStockQuantity() + quantity);
            if (!products.contains(p)) {
                products.add(p);
            }
            System.out.println("Supplier " + name + " supplied " + quantity + " of " + p.getName());
        }
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }
}
