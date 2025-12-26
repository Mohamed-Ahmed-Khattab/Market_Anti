package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Cart;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Cart entity
 */
public class CartDAO {
    private final DatabaseManager dbManager;

    public CartDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Cart cart) {
        String sql = "INSERT INTO Cart (customerID, status) VALUES (?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cart.getCustomerID());
            stmt.setString(2, cart.getStatus() != null ? cart.getStatus() : "active");

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cart.setCartID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Cart getById(int cartID) {
        String sql = "SELECT * FROM Cart WHERE cartID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCartFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Cart getActiveCartByCustomer(int customerID) {
        String sql = "SELECT * FROM Cart WHERE customerID = ? AND status = 'active' ORDER BY createdAt DESC LIMIT 1";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractCartFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Cart> getByCustomer(int customerID) {
        List<Cart> carts = new ArrayList<>();
        String sql = "SELECT * FROM Cart WHERE customerID = ? ORDER BY createdAt DESC";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                carts.add(extractCartFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carts;
    }

    public boolean updateStatus(int cartID, String status) {
        String sql = "UPDATE Cart SET status = ?, updatedAt = CURRENT_TIMESTAMP WHERE cartID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, cartID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int cartID) {
        String sql = "DELETE FROM Cart WHERE cartID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Cart extractCartFromResultSet(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setCartID(rs.getInt("cartID"));
        cart.setCustomerID(rs.getInt("customerID"));

        Timestamp createdAt = rs.getTimestamp("createdAt");
        if (createdAt != null) {
            cart.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        if (updatedAt != null) {
            cart.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        cart.setStatus(rs.getString("status"));

        return cart;
    }
}
