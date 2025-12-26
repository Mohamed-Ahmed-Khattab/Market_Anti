package com.example.demo.model;

import com.example.demo.db.DatabaseManager;
import com.example.demo.dto.OrderDetailsDTO;
import com.example.demo.dto.TransactionDTO;
import lombok.Data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PlaceOrderModel - handles order placement and persistence.
 * Orders are stored as Carts with status 'checked_out' and CartItems.
 */
@Data
public class PlaceOrderModel {
    private static final DatabaseManager dbManager = DatabaseManager.getInstance();

    public static int generateOrderId() {
        // Get the next order ID from the database
        String sql = "SELECT COALESCE(MAX(cartID), 1000) + 1 FROM Cart";
        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return (int) (System.currentTimeMillis() % 100000);
    }

    public static boolean placeOrder(TransactionDTO transactionDTO) throws SQLException {
        Connection conn = null;
        try {
            conn = dbManager.getConnection();
            conn.setAutoCommit(false);

            // Create a cart entry with status 'checked_out'
            // Using customerID = 1 as default (guest customer)
            String insertCartSQL = "INSERT INTO Cart (customerID, status, createdAt, updatedAt) VALUES (?, 'checked_out', NOW(), NOW())";
            int cartId;

            try (PreparedStatement cartStmt = conn.prepareStatement(insertCartSQL, Statement.RETURN_GENERATED_KEYS)) {
                cartStmt.setInt(1, 1); // Default customer ID
                cartStmt.executeUpdate();

                try (ResultSet keys = cartStmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        cartId = keys.getInt(1);
                    } else {
                        throw new SQLException("Failed to create cart");
                    }
                }
            }

            // Insert cart items and update stock
            String insertItemSQL = "INSERT INTO CartItem (cartID, productID, quantity, priceAtAdd) VALUES (?, ?, ?, ?)";
            String updateStockSQL = "UPDATE Product SET stockQuantity = stockQuantity - ? WHERE productID = ?";
            String getPriceSQL = "SELECT price FROM Product WHERE productID = ?";

            for (OrderDetailsDTO details : transactionDTO.getOrderDetailList()) {
                // Get product price
                double price = 0;
                try (PreparedStatement priceStmt = conn.prepareStatement(getPriceSQL)) {
                    priceStmt.setInt(1, details.getProductId());
                    ResultSet rs = priceStmt.executeQuery();
                    if (rs.next()) {
                        price = rs.getDouble("price");
                    }
                }

                // Insert cart item
                try (PreparedStatement itemStmt = conn.prepareStatement(insertItemSQL)) {
                    itemStmt.setInt(1, cartId);
                    itemStmt.setInt(2, details.getProductId());
                    itemStmt.setInt(3, details.getQty());
                    itemStmt.setDouble(4, price);
                    itemStmt.executeUpdate();
                }

                // Update stock
                try (PreparedStatement stockStmt = conn.prepareStatement(updateStockSQL)) {
                    stockStmt.setInt(1, details.getQty());
                    stockStmt.setInt(2, details.getProductId());
                    stockStmt.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<OrderDetailsDTO> searchOrderById(int orderId) throws SQLException {
        List<OrderDetailsDTO> orderDetails = new ArrayList<>();
        String sql = """
                SELECT ci.productID, ci.quantity
                FROM CartItem ci
                WHERE ci.cartID = ?
                """;

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orderDetails.add(OrderDetailsDTO.builder()
                        .transactionId(orderId)
                        .productId(rs.getInt("productID"))
                        .qty(rs.getInt("quantity"))
                        .build());
            }
        }
        return orderDetails;
    }
}
