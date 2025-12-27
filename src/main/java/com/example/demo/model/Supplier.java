package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Supplier {
    private int supplierID;
    private static int lastID = 0;
    private List<Product> products;
    private String name;
    private double rating;
    private boolean active;
    private String productsAttribute; // For multivalued attribute in DB

    // Additional fields for DAO
    private String supplierName; // Alias for name or specific field
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String country;

    public Supplier() {
        this.supplierID = ++lastID;
        this.products = new ArrayList<>();
        this.active = true;
    }

    public Supplier(String name, double rating) {
        this.supplierID = ++lastID;
        this.name = name;
        this.supplierName = name; // Sync
        this.rating = rating;
        this.active = true;
        this.products = new ArrayList<>();
    }

    public void addProduct(Product p) {
        if (products == null) {
            products = new ArrayList<>();
        }
        if (!products.contains(p)) {
            products.add(p);
        }
    }

    public void supplyProduct(Product p, int quantity) {
        if (active && p != null) {
            p.updateStock(p.getStockQuantity() + quantity);
            addProduct(p);
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

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setName(String name) {
        this.name = name;
        this.supplierName = name;
    }

    public String getName() {
        return name;
    }

    // DAO Compatibility Accessors

    public String getSupplierName() {
        return supplierName != null ? supplierName : name;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
        this.name = supplierName;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

    public String getProductsAttribute() {
        return productsAttribute;
    }

    public void setProductsAttribute(String productsAttribute) {
        this.productsAttribute = productsAttribute;
    }
}
