package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.tm.User;
import com.example.demo.util.UserType;

import java.sql.*;

/**
 * Data Access Object for User entity (Authentication)
 */
public class UserDAO {
    private final DatabaseManager dbManager;

    public UserDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(User user) {
        String sql = "INSERT INTO Users (email, password, firstName, lastName, userType) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getUserType().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public User authenticate(String email, String password) {
        // 1. Check Users table (System Admins/Cashiers)
        String sqlUsers = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlUsers)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            handleAuthException(e, "Users");
        }

        // 2. Check Employee table
        String sqlEmployee = "SELECT * FROM Employee WHERE email = ? AND password = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlEmployee)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return User.builder()
                        .id(rs.getInt("employeeID"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .firstName(rs.getString("firstName"))
                        .lastName(rs.getString("lastName"))
                        .userType(rs.getBoolean("isManager") ? UserType.ADMIN : UserType.CASHIER)
                        .build();
            }
        } catch (SQLException e) {
            handleAuthException(e, "Employee");
        }

        // 3. Check Customer table
        String sqlCustomer = "SELECT * FROM Customer WHERE email = ? AND password = ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sqlCustomer)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return User.builder()
                        .id(rs.getInt("customerID"))
                        .email(rs.getString("email"))
                        .password(rs.getString("password"))
                        .firstName(rs.getString("firstName"))
                        .lastName(rs.getString("lastName"))
                        .userType(UserType.CUSTOMER)
                        .build();
            }
        } catch (SQLException e) {
            handleAuthException(e, "Customer");
        }

        return null;
    }

    private void handleAuthException(SQLException e, String tableName) {
        if (e.getMessage().contains("Unknown column 'password'")) {
            System.err.println("CRITICAL ERROR: The '" + tableName + "' table is missing the 'password' column.");
            System.err.println("Please run: ALTER TABLE " + tableName
                    + " ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT 'password123';");
        } else {
            e.printStackTrace();
        }
    }

    public boolean existsByEmail(String email) {
        String[] queries = {
                "SELECT COUNT(*) FROM Users WHERE email = ?",
                "SELECT COUNT(*) FROM Employee WHERE email = ?",
                "SELECT COUNT(*) FROM Customer WHERE email = ?"
        };

        for (String sql : queries) {
            try (Connection conn = dbManager.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getInt("userID"))
                .email(rs.getString("email"))
                .password(rs.getString("password"))
                .firstName(rs.getString("firstName"))
                .lastName(rs.getString("lastName"))
                .userType(UserType.valueOf(rs.getString("userType")))
                .build();
    }
}
