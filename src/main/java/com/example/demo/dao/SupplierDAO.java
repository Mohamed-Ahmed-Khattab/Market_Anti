package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Supplier entity
 */
public class SupplierDAO {
    private final DatabaseManager dbManager;

    public SupplierDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Supplier supplier) {
        String sql = "INSERT INTO Supplier (supplierName, contactPerson, email, phoneNumber, address, city, country, rating) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getPhoneNumber());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getCity());
            stmt.setString(7, supplier.getCountry());
            stmt.setDouble(8, supplier.getRating());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        supplier.setSupplierID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Supplier getById(int supplierID) {
        String sql = "SELECT * FROM Supplier WHERE supplierID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractSupplierFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Supplier> getAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT * FROM Supplier ORDER BY supplierName";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                suppliers.add(extractSupplierFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return suppliers;
    }

    public boolean update(Supplier supplier) {
        String sql = "UPDATE Supplier SET supplierName = ?, contactPerson = ?, email = ?, phoneNumber = ?, address = ?, city = ?, country = ?, rating = ? WHERE supplierID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, supplier.getSupplierName());
            stmt.setString(2, supplier.getContactPerson());
            stmt.setString(3, supplier.getEmail());
            stmt.setString(4, supplier.getPhoneNumber());
            stmt.setString(5, supplier.getAddress());
            stmt.setString(6, supplier.getCity());
            stmt.setString(7, supplier.getCountry());
            stmt.setDouble(8, supplier.getRating());
            stmt.setInt(9, supplier.getSupplierID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int supplierID) {
        String sql = "DELETE FROM Supplier WHERE supplierID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Supplier extractSupplierFromResultSet(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        int id = rs.getInt("supplierID");
        supplier.setSupplierID(id);
        supplier.setSupplierName(rs.getString("supplierName"));
        supplier.setContactPerson(rs.getString("contactPerson"));
        supplier.setEmail(rs.getString("email"));
        supplier.setPhoneNumber(rs.getString("phoneNumber"));
        supplier.setAddress(rs.getString("address"));
        supplier.setCity(rs.getString("city"));
        supplier.setCountry(rs.getString("country"));
        supplier.setRating(rs.getDouble("rating"));

        // Multivalued attribute 'products' implementation linked via SupplierProduct
        // table
        try {
            ProductDAO productDAO = new ProductDAO();
            supplier.setProducts(productDAO.getProductsLinkedToSupplier(id));
        } catch (Exception e) {
            System.err.println("Error fetching products for supplier " + id + ": " + e.getMessage());
        }

        return supplier;
    }
}
