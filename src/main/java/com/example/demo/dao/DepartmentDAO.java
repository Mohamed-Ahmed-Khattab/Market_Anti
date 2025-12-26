package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Department entity
 * Adapted to match the Department model
 */
public class DepartmentDAO {
    private final DatabaseManager dbManager;

    public DepartmentDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Department department) {
        String sql = "INSERT INTO Department (departmentName, location, budget) VALUES (?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, department.getName());

            String loc = "";
            if (department.getLocation() != null && !department.getLocation().isEmpty()) {
                loc = department.getLocation().get(0);
            }
            stmt.setString(2, loc);

            stmt.setDouble(3, department.getBudget());

            // Manager handling is complex as Model assumes Manager object, DB assumes ID
            // Ignoring Manager for Creation to avoid circular dependency/complexity

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                // Cannot set ID on Model as it has no ID field.
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Department getById(int departmentID) {
        String sql = "SELECT * FROM Department WHERE departmentID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractDepartmentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Department> getAll() {
        List<Department> departments = new ArrayList<>();
        String sql = "SELECT * FROM Department ORDER BY departmentName";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                departments.add(extractDepartmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return departments;
    }

    public boolean update(Department department) {
        String sql = "UPDATE Department SET departmentName = ?, location = ?, budget = ? WHERE departmentID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, department.getName());

            String loc = "";
            if (department.getLocation() != null && !department.getLocation().isEmpty()) {
                loc = department.getLocation().get(0);
            }
            stmt.setString(2, loc);
            stmt.setDouble(3, department.getBudget());
            stmt.setInt(4, department.getDepartmentID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int departmentID) {
        String sql = "DELETE FROM Department WHERE departmentID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Department extractDepartmentFromResultSet(ResultSet rs) throws SQLException {
        String name = rs.getString("departmentName");
        String location = rs.getString("location");
        double budget = rs.getDouble("budget");

        // Constructor: double budget, String name, String status
        Department dept = new Department(budget, name, "Active");

        // Set the departmentID from database for edit/delete operations
        dept.setId(rs.getInt("departmentID"));

        if (location != null) {
            List<String> locs = new ArrayList<>();
            locs.add(location);
            dept.setLocation(locs);
        }

        return dept;
    }
}
