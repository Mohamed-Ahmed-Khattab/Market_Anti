package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public class CartDTO {
    private int cartID;
    private String status;
    private LocalDate creationDate;
    private double totalPrice;
    private List<CartItemDTO> items;

    public CartDTO(int cartID, String status, LocalDate creationDate, double totalPrice, List<CartItemDTO> items) {
        this.cartID = cartID;
        this.status = status;
        this.creationDate = creationDate;
        this.totalPrice = totalPrice;
        this.items = items;
    }

    // Getters and Setters matching UML attributes
    public int getCartID() { return cartID; }
    public String getStatus() { return status; }
    public LocalDate getCreationDate() { return creationDate; }
    public double getTotalPrice() { return totalPrice; }
    public List<CartItemDTO> getItems() { return items; }
}
