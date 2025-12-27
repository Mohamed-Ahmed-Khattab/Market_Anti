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
        String sql = "INSERT INTO Customer (firstName, lastName, email, phoneNumber, address, city, state, zipCode, country, loyaltyPoints, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String[] names = splitName(customer.getName());
            stmt.setString(1, names[0]);
            stmt.setString(2, names[1]);
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhoneNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getCity());
            stmt.setString(7, customer.getState());
            stmt.setString(8, customer.getZipCode());
            stmt.setString(9, customer.getCountry() != null ? customer.getCountry() : "Egypt");
            stmt.setInt(10, customer.getLoyaltyPoints());
            stmt.setString(11, customer.getPassword());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        customer.setCustomerID(generatedKeys.getInt(1));
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
        String sql = "UPDATE Customer SET firstName = ?, lastName = ?, email = ?, phoneNumber = ?, address = ?, city = ?, state = ?, zipCode = ?, country = ?, loyaltyPoints = ?, password = ? WHERE customerID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String[] names = splitName(customer.getName());
            stmt.setString(1, names[0]);
            stmt.setString(2, names[1]);
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhoneNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getCity());
            stmt.setString(7, customer.getState());
            stmt.setString(8, customer.getZipCode());
            stmt.setString(9, customer.getCountry());
            stmt.setInt(10, customer.getLoyaltyPoints());
            stmt.setString(11, customer.getPassword());
            stmt.setInt(12, customer.getCustomerID());

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

        Customer customer = new Customer(name.trim());
        customer.setCustomerID(rs.getInt("customerID"));
        customer.setEmail(rs.getString("email"));
        customer.setPhoneNumber(rs.getString("phoneNumber"));
        customer.setAddress(rs.getString("address"));
        customer.setCity(rs.getString("city"));
        customer.setState(rs.getString("state"));
        customer.setZipCode(rs.getString("zipCode"));
        customer.setCountry(rs.getString("country"));
        customer.setLoyaltyPoints(rs.getInt("loyaltyPoints"));
        customer.setPassword(rs.getString("password"));

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
