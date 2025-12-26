package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * CartItem entity representing items in a shopping cart
 * Maps to the CartItem table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Integer cartItemID;
    private Integer cartID;
    private Integer productID;
    private Integer quantity;
    private BigDecimal priceAtAdd;
    private LocalDateTime addedAt;

    // Constructor without ID for new cart items
    public CartItem(Integer cartID, Integer productID, Integer quantity, BigDecimal priceAtAdd) {
        this.cartID = cartID;
        this.productID = productID;
        this.quantity = quantity;
        this.priceAtAdd = priceAtAdd;
        this.addedAt = LocalDateTime.now();
    }

    /**
     * Calculate total price for this cart item
     */
    public BigDecimal getTotalPrice() {
        if (quantity != null && priceAtAdd != null) {
            return priceAtAdd.multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }

    // UML Methods
    public void increaseQuantity(int amount) {
        if (this.quantity == null)
            this.quantity = 0;
        this.quantity += amount;
    }

    public void decreaseQuantity(int amount) {
        if (this.quantity != null && this.quantity >= amount) {
            this.quantity -= amount;
        }
    }
}
