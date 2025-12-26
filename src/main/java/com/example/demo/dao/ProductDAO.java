package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data Access Object for Product entity
 * Adapted to match the simplified Product model
 */
public class ProductDAO {
    private final DatabaseManager dbManager;

    public ProductDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Product product) {
        // Insert with defaults for missing model fields
        String sql = "INSERT INTO Product (productName, category, price, stockQuantity, barcode, sku, isActive) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, "General"); // Default category
            stmt.setDouble(3, product.getPrice());
            stmt.setInt(4, product.getStockQuantity());

            // Generate unique placeholders for unique constraints
            String uniqueId = UUID.randomUUID().toString();
            stmt.setString(5, uniqueId.substring(0, 12)); // Barcode
            stmt.setString(6, "SKU-" + uniqueId.substring(0, 8)); // SKU

            stmt.setBoolean(7, true); // Active

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        product.setProductID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Product getById(int productID) {
        String sql = "SELECT * FROM Product WHERE productID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getAll() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE isActive = true ORDER BY productName";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean update(Product product) {
        // Only update fields that exist in the Model
        String sql = "UPDATE Product SET productName = ?, price = ?, stockQuantity = ? WHERE productID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setDouble(2, product.getPrice());
            stmt.setInt(3, product.getStockQuantity());
            stmt.setInt(4, product.getProductID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int productID) {
        String sql = "UPDATE Product SET isActive = false WHERE productID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("productID");
        String name = rs.getString("productName");
        int stock = rs.getInt("stockQuantity");
        double price = rs.getDouble("price");

        // Constructor: productID, name, stockQuantity, price
        return new Product(id, name, stock, price);
    }

    public List<Product> getLowStock() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE isActive = true AND stockQuantity < 10 ORDER BY stockQuantity ASC";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    public boolean updateStock(int productID, int quantityChange) {
        String sql = "UPDATE Product SET stockQuantity = stockQuantity + ? WHERE productID = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quantityChange);
            stmt.setInt(2, productID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
