package com.example.demo.util;

/**
 * Holds information about a dynamically discounted product.
 * Used for market trend analysis where the most sold product
 * gets a 5% discount without modifying the database.
 */
public class DiscountedProduct {
    private final int productID;
    private final String productName;
    private final double originalPrice;
    private final double discountedPrice;
    private static final double DISCOUNT_PERCENTAGE = 0.05; // 5% discount

    public DiscountedProduct(int productID, String productName, double originalPrice) {
        this.productID = productID;
        this.productName = productName;
        this.originalPrice = originalPrice;
        this.discountedPrice = originalPrice * (1 - DISCOUNT_PERCENTAGE);
    }

    public int getProductID() {
        return productID;
    }

    public String getProductName() {
        return productName;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getDiscountedPrice() {
        return discountedPrice;
    }

    public double getDiscountPercentage() {
        return DISCOUNT_PERCENTAGE * 100;
    }

    @Override
    public String toString() {
        return String.format("DiscountedProduct{id=%d, name='%s', original=%.2f, discounted=%.2f}",
                productID, productName, originalPrice, discountedPrice);
    }
}
