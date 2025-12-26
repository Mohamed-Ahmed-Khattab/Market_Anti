package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for Customer entity
 * Adapted to match the simplified Customer model
 */
public class CustomerDAO {
    private final DatabaseManager dbManager;

    public CustomerDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Customer customer) {
        String sql = "INSERT INTO Customer (firstName, lastName, email, phoneNumber, address, loyaltyPoints) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String[] names = splitName(customer.getName());
            stmt.setString(1, names[0]);
            stmt.setString(2, names[1]);
            // Generate a unique dummy email since Model doesn't have it but DB requires
            // UNIQUE NOT NULL
            stmt.setString(3, "user_" + UUID.randomUUID().toString().substring(0, 8) + "@placeholder.com");
            stmt.setString(4, "N/A"); // Phone is not in model, nullable in DB
            stmt.setString(5, customer.getAddress());
            stmt.setInt(6, 0); // No loyalty points in model

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Customer has no setID method? It inherits from Person, check if it has a
                        // setter for SSN or ID?
                        // Person has 'ssn' (string) and 'lastSSN'. No integer ID.
                        // We might lose the ID here unless we map it to something.
                        // For now, we proceed. Usage of ID in controller might need to change.
                        // customer.setCustomerID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Customer getById(int customerID) {
        String sql = "SELECT * FROM Customer WHERE customerID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Customer> getAll() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM Customer ORDER BY lastName, firstName";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                customers.add(extractCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public boolean update(Customer customer) {
        String sql = "UPDATE Customer SET firstName = ?, lastName = ?, address = ? WHERE customerID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String[] names = splitName(customer.getName());
            stmt.setString(1, names[0]);
            stmt.setString(2, names[1]);
            stmt.setString(3, customer.getAddress());
            stmt.setInt(4, customer.getCustomerID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int customerID) {
        String sql = "DELETE FROM Customer WHERE customerID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Customer extractCustomerFromResultSet(ResultSet rs) throws SQLException {
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String name = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        String address = rs.getString("address");

        // Constructor: Name, Gender, Address, DOB, isPremium, Balance
        // We infer default values for missing data
        Customer customer = new Customer(
                name.trim(),
                "Unknown", // Gender not in DB
                address,
                null, // DOB not in DB
                false, // isPremium default
                0.0 // Balance default
        );

        // Set the customerID from database for edit/delete operations
        customer.setCustomerID(rs.getInt("customerID"));

        return customer;
    }

    private String[] splitName(String fullName) {
        if (fullName == null || fullName.isEmpty())
            return new String[] { "Unknown", "Unknown" };
        String[] parts = fullName.trim().split("\\s+", 2);
        if (parts.length == 1)
            return new String[] { parts[0], "" };
        return parts;
    }

    public boolean updateLoyaltyPoints(int customerID, int points) {
        String sql = "UPDATE Customer SET loyaltyPoints = loyaltyPoints + ? WHERE customerID = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, points);
            stmt.setInt(2, customerID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
