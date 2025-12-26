package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.CartItem;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for CartItem entity
 */
public class CartItemDAO {
    private final DatabaseManager dbManager;

    public CartItemDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(CartItem cartItem) {
        String sql = "INSERT INTO CartItem (cartID, productID, quantity, priceAtAdd) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, cartItem.getCartID());
            stmt.setInt(2, cartItem.getProductID());
            stmt.setInt(3, cartItem.getQuantity());
            stmt.setDouble(4, cartItem.getPriceAtAdd());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        cartItem.setCartItemID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<CartItem> getByCart(int cartID) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT * FROM CartItem WHERE cartID = ? ORDER BY addedAt";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                items.add(extractCartItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public boolean updateQuantity(int cartItemID, int quantity) {
        String sql = "UPDATE CartItem SET quantity = ? WHERE cartItemID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, quantity);
            stmt.setInt(2, cartItemID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int cartItemID) {
        String sql = "DELETE FROM CartItem WHERE cartItemID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartItemID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteByCart(int cartID) {
        String sql = "DELETE FROM CartItem WHERE cartID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public BigDecimal getCartTotal(int cartID) {
        String sql = "SELECT SUM(quantity * priceAtAdd) as total FROM CartItem WHERE cartID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cartID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                BigDecimal total = rs.getBigDecimal("total");
                return total != null ? total : BigDecimal.ZERO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    private CartItem extractCartItemFromResultSet(ResultSet rs) throws SQLException {
        CartItem item = new CartItem();
        item.setCartItemID(rs.getInt("cartItemID"));
        item.setCartID(rs.getInt("cartID"));
        item.setProductID(rs.getInt("productID"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPriceAtAdd(rs.getDouble("priceAtAdd"));

        Timestamp addedAt = rs.getTimestamp("addedAt");
        if (addedAt != null) {
            item.setAddedAt(addedAt.toLocalDateTime());
        }

        return item;
    }
}
