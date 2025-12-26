package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Product;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Product entity
 */
public class ProductDAO {
    private final DatabaseManager dbManager;

    public ProductDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Product product) {
        String sql = "INSERT INTO Product (productName, description, category, price, cost, stockQuantity, reorderLevel, supplierID, barcode, sku, weight, dimensions, expiryDate, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setBigDecimal(5, product.getCost());
            stmt.setInt(6, product.getStockQuantity() != null ? product.getStockQuantity() : 0);
            stmt.setInt(7, product.getReorderLevel() != null ? product.getReorderLevel() : 10);

            if (product.getSupplierID() != null) {
                stmt.setInt(8, product.getSupplierID());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, product.getBarcode());
            stmt.setString(10, product.getSku());
            stmt.setBigDecimal(11, product.getWeight());
            stmt.setString(12, product.getDimensions());

            if (product.getExpiryDate() != null) {
                stmt.setDate(13, Date.valueOf(product.getExpiryDate()));
            } else {
                stmt.setNull(13, Types.DATE);
            }

            stmt.setBoolean(14, product.getIsActive() != null ? product.getIsActive() : true);

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

    public List<Product> getByCategory(String category) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE category = ? AND isActive = true ORDER BY productName";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getLowStock() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE stockQuantity <= reorderLevel AND isActive = true ORDER BY stockQuantity";

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
        String sql = "UPDATE Product SET productName = ?, description = ?, category = ?, price = ?, cost = ?, stockQuantity = ?, reorderLevel = ?, supplierID = ?, barcode = ?, sku = ?, weight = ?, dimensions = ?, expiryDate = ?, isActive = ? WHERE productID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getProductName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory());
            stmt.setBigDecimal(4, product.getPrice());
            stmt.setBigDecimal(5, product.getCost());
            stmt.setInt(6, product.getStockQuantity());
            stmt.setInt(7, product.getReorderLevel());

            if (product.getSupplierID() != null) {
                stmt.setInt(8, product.getSupplierID());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, product.getBarcode());
            stmt.setString(10, product.getSku());
            stmt.setBigDecimal(11, product.getWeight());
            stmt.setString(12, product.getDimensions());

            if (product.getExpiryDate() != null) {
                stmt.setDate(13, Date.valueOf(product.getExpiryDate()));
            } else {
                stmt.setNull(13, Types.DATE);
            }

            stmt.setBoolean(14, product.getIsActive());
            stmt.setInt(15, product.getProductID());

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

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductID(rs.getInt("productID"));
        product.setProductName(rs.getString("productName"));
        product.setDescription(rs.getString("description"));
        product.setCategory(rs.getString("category"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setCost(rs.getBigDecimal("cost"));
        product.setStockQuantity(rs.getInt("stockQuantity"));
        product.setReorderLevel(rs.getInt("reorderLevel"));

        int supplierID = rs.getInt("supplierID");
        if (!rs.wasNull()) {
            product.setSupplierID(supplierID);
        }

        product.setBarcode(rs.getString("barcode"));
        product.setSku(rs.getString("sku"));
        product.setWeight(rs.getBigDecimal("weight"));
        product.setDimensions(rs.getString("dimensions"));

        Date expiryDate = rs.getDate("expiryDate");
        if (expiryDate != null) {
            product.setExpiryDate(expiryDate.toLocalDate());
        }

        product.setIsActive(rs.getBoolean("isActive"));

        Timestamp createdAt = rs.getTimestamp("createdAt");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updatedAt");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return product;
    }
}
