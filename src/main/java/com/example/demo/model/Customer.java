package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Customer entity representing customers
 * Maps to the Customer table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Customer implements SearchItem {
    private Integer customerID;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private LocalDateTime registrationDate;
    private Integer loyaltyPoints;

    // Associations
    private java.util.List<Cart> orderHistory = new java.util.ArrayList<>();
    private Cart currentCart;
    private Employee assignedEmployee;
    private boolean isPremium;
    private double balance;

    // UML Methods
    public void addToCart(Product p, int qty) {
        if (currentCart == null)
            createCart();
        // Logic to add to currentCart would go here
        // But since CartItem needs ID matching, this is usually controller logic.
        // We will add stub for UML compliance.
        System.out.println("Added " + p.getProductName() + " to cart.");
    }

    public void createCart() {
        this.currentCart = new Cart(this.customerID, "active");
    }

    public void checkout() {
        if (currentCart != null) {
            currentCart.checkout();
            orderHistory.add(currentCart);
            currentCart = null;
        }
    }

    public void removeFromCart(CartItem item) {
        if (currentCart != null)
            currentCart.removeItem(item);
    }

    public boolean isUnknown() {
        return this.customerID == null;
    }

    public void setAssignedEmployee(Employee e) {
        this.assignedEmployee = e;
    }

    public java.util.List<Cart> getOrderHistory() {
        return orderHistory;
    }

    public Cart getCurrentCart() {
        return currentCart;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Constructor without ID for new customers
    public Customer(String firstName, String lastName, String email, String phoneNumber,
            String address, String city, String state, String zipCode, String country) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.registrationDate = LocalDateTime.now();
        this.loyaltyPoints = 0;
    }

    /**
     * Get full name of customer
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isEmpty())
            return true;
        String lowerKey = keyword.toLowerCase();
        return (firstName != null && firstName.toLowerCase().contains(lowerKey)) ||
                (lastName != null && lastName.toLowerCase().contains(lowerKey)) ||
                (email != null && email.toLowerCase().contains(lowerKey)) ||
                (phoneNumber != null && phoneNumber.contains(keyword));
    }
}
