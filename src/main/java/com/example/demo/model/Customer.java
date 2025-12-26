package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Customer extends Person {
    private List<Cart> orderHistory;
    private Cart currentCart;
    private Employee assignedEmployee;
    private boolean isPremium;
    private double balance;

    public Customer(String name, String gender, String address, LocalDate dob, boolean isPremium, double balance) {
        super(name, gender, address, dob);
        this.isPremium = isPremium;
        this.balance = balance;
        this.orderHistory = new ArrayList<>();
        this.currentCart = new Cart();
    }

    // Default constructor for unknown maybe?
    public Customer(String name) {
        super(name, "Unknown", "Unknown", null);
        this.isPremium = false;
        this.balance = 0;
        this.orderHistory = new ArrayList<>();
        this.currentCart = new Cart();
    }

    public void addToCart(CartItem item) {
        if (currentCart == null)
            createCart();
        currentCart.addItem(item);
    }

    public void removeFromCart(CartItem item) {
        if (currentCart != null) {
            currentCart.removeItem(item);
        }
    }

    public void createCart() {
        this.currentCart = new Cart();
        this.currentCart.setStatus("Active");
    }

    public boolean isUnknown() {
        return "Unknown".equals(getName());
    }

    public void checkout() {
        if (currentCart != null && !currentCart.isEmpty()) {
            double total = currentCart.calculateTotalPrice();
            if (balance >= total) {
                balance -= total;
                currentCart.setStatus("Completed");
                orderHistory.add(currentCart);
                createCart(); // New empty cart
                System.out.println("Checkout successful. Remaining balance: " + balance);
            } else {
                System.out.println("Insufficient balance.");
            }
        }
    }

    public void setAssignedEmployee(Employee e) {
        this.assignedEmployee = e;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public List<Cart> getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(List<Cart> orderHistory) {
        this.orderHistory = orderHistory;
    }

    public Cart getCurrentCart() {
        return currentCart;
    }

    public void setCurrentCart(Cart currentCart) {
        this.currentCart = currentCart;
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

    @Override
    public String getRole() {
        return "Customer";
    }
}
