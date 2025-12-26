package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Product entity representing inventory items
 * Maps to the Product table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product implements SearchItem {
    private Integer productID;
    private String productName;
    private String description;
    private String category;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer stockQuantity;
    private Integer reorderLevel;
    private Integer supplierID;
    private String barcode;
    private String sku;
    private BigDecimal weight;
    private String dimensions;
    private LocalDate expiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor without ID for new products
    public Product(String productName, String description, String category, BigDecimal price,
            BigDecimal cost, Integer stockQuantity, Integer reorderLevel, Integer supplierID,
            String barcode, String sku) {
        this.productName = productName;
        this.description = description;
        this.category = category;
        this.price = price;
        this.cost = cost;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
        this.supplierID = supplierID;
        this.barcode = barcode;
        this.sku = sku;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if product needs reordering
     */
    public boolean needsReorder() {
        return stockQuantity != null && reorderLevel != null && stockQuantity <= reorderLevel;
    }

    // UML Methods
    public String getStatusColor() {
        if (stockQuantity <= 0)
            return "RED";
        if (stockQuantity <= reorderLevel)
            return "YELLOW";
        return "GREEN";
    }

    public boolean isOutOfStock() {
        return stockQuantity == null || stockQuantity <= 0;
    }

    public void updateStock(int qty) {
        this.stockQuantity = qty;
    }

    public boolean reduceStock(int quantity) {
        if (this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
            return true;
        }
        return false;
    }

    public void addSubstitute(Product p) {
        // Logic to add substitute
    }

    // Standard Getters/Setters matching UML implies
    public void setStockQuantity(int quantity) {
        this.stockQuantity = quantity;
    }

    public int getStockQuantity() {
        return (stockQuantity != null) ? stockQuantity : 0;
    }

    public void setPrice(double price) {
        this.price = BigDecimal.valueOf(price);
    }

    public double getPrice() {
        return (price != null) ? price.doubleValue() : 0.0;
    }

    public void setName(String name) {
        this.productName = name;
    }

    public String getName() {
        return productName;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isEmpty())
            return true;
        String lowerKey = keyword.toLowerCase();
        return (productName != null && productName.toLowerCase().contains(lowerKey)) ||
                (category != null && category.toLowerCase().contains(lowerKey)) ||
                (sku != null && sku.toLowerCase().contains(lowerKey)) ||
                (barcode != null && barcode.contains(keyword));
    }
}
