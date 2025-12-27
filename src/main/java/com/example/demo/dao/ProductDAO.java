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
        String sql = "INSERT INTO Product (productName, description, category, price, cost, stockQuantity, reorderLevel, supplierID, barcode, sku, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory() != null ? product.getCategory() : "General");
            stmt.setDouble(4, product.getPrice());
            stmt.setDouble(5, product.getCost());
            stmt.setInt(6, product.getStockQuantity());
            stmt.setInt(7, product.getReorderLevel());

            if (product.getSupplierID() > 0) {
                stmt.setInt(8, product.getSupplierID());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, product.getBarcode());
            stmt.setString(10, product.getSku());
            stmt.setBoolean(11, true); // Active

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
        String sql = "UPDATE Product SET productName = ?, description = ?, category = ?, price = ?, cost = ?, stockQuantity = ?, reorderLevel = ?, supplierID = ?, barcode = ?, sku = ? WHERE productID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setString(3, product.getCategory());
            stmt.setDouble(4, product.getPrice());
            stmt.setDouble(5, product.getCost());
            stmt.setInt(6, product.getStockQuantity());
            stmt.setInt(7, product.getReorderLevel());

            if (product.getSupplierID() > 0) {
                stmt.setInt(8, product.getSupplierID());
            } else {
                stmt.setNull(8, Types.INTEGER);
            }

            stmt.setString(9, product.getBarcode());
            stmt.setString(10, product.getSku());
            stmt.setInt(11, product.getProductID());

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
        Product p = new Product();
        p.setProductID(rs.getInt("productID"));
        p.setName(rs.getString("productName"));
        p.setDescription(rs.getString("description"));
        p.setCategory(rs.getString("category"));
        p.setPrice(rs.getDouble("price"));
        p.setCost(rs.getDouble("cost"));
        p.setStockQuantity(rs.getInt("stockQuantity"));
        p.setReorderLevel(rs.getInt("reorderLevel"));
        p.setSupplierID(rs.getInt("supplierID"));
        p.setBarcode(rs.getString("barcode"));
        p.setSku(rs.getString("sku"));
        return p;
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

    public Product getByName(String productName) {
        String sql = "SELECT * FROM Product WHERE productName = ? AND isActive = true";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createWithSupplier(Product product, int supplierID) {
        String sql = "INSERT INTO Product (productName, description, category, price, cost, stockQuantity, reorderLevel, supplierID, barcode, sku, isActive) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription() != null ? product.getDescription() : "Supplied through order");
            stmt.setString(3, product.getCategory() != null ? product.getCategory() : "General");
            stmt.setDouble(4, product.getPrice());
            stmt.setDouble(5, product.getCost());
            stmt.setInt(6, product.getStockQuantity());
            stmt.setInt(7, product.getReorderLevel() > 0 ? product.getReorderLevel() : 10);
            stmt.setInt(8, supplierID);

            String barcode = product.getBarcode();
            String sku = product.getSku();
            if (barcode == null || barcode.isEmpty()) {
                barcode = UUID.randomUUID().toString().substring(0, 12);
            }
            if (sku == null || sku.isEmpty()) {
                sku = "SKU-" + UUID.randomUUID().toString().substring(0, 8);
            }

            stmt.setString(9, barcode);
            stmt.setString(10, sku);
            stmt.setBoolean(11, true);

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

    public List<Product> getProductsBySupplierId(int supplierID) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE supplierID = ? AND isActive = true";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getProductsLinkedToSupplier(int supplierID) {
        List<Product> products = new ArrayList<>();
        // Join Product and SupplierProduct
        String sql = """
                    SELECT p.*
                    FROM Product p
                    JOIN SupplierProduct sp ON p.productID = sp.productID
                    WHERE sp.supplierID = ? AND p.isActive = true
                    ORDER BY p.productName
                """;

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                products.add(extractProductFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
}
