package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Supplier entity representing product suppliers
 * Maps to the Supplier table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Supplier implements SearchItem {
    private Integer supplierID;
    private String supplierName;
    private String contactPerson;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String country;
    private BigDecimal rating;
    private boolean active = true;
    private java.util.List<Product> products = new java.util.ArrayList<>();

    // UML Methods
    public void supplyProduct(Product p, int quantity) {
        p.updateStock(p.getStockQuantity() + quantity);
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

    public java.util.List<Product> getProducts() {
        return products;
    }

    public void setProducts(java.util.List<Product> products) {
        this.products = products;
    }

    // Constructor without ID for new suppliers
    public Supplier(String supplierName, String contactPerson, String email, String phoneNumber,
            String address, String city, String country, BigDecimal rating) {
        this.supplierName = supplierName;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.country = country;
        this.rating = rating;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isEmpty())
            return true;
        String lowerKey = keyword.toLowerCase();
        return (supplierName != null && supplierName.toLowerCase().contains(lowerKey)) ||
                (contactPerson != null && contactPerson.toLowerCase().contains(lowerKey)) ||
                (email != null && email.toLowerCase().contains(lowerKey)) ||
                (phoneNumber != null && phoneNumber.contains(keyword));
    }
}
