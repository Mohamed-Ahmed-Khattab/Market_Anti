package com.example.demo.dto;

public class CartItemDTO {
    private int cartItemID;
    private int productId; // Reference to Product
    private int quantity;
    private double unitPrice;

    public CartItemDTO(int cartItemID, int productId, int quantity, double unitPrice) {
        this.cartItemID = cartItemID;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public int getCartItemID() { return cartItemID; }
    public int getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
}
