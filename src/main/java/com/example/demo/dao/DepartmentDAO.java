package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Department;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Department entity
 * Handles all database operations for departments
 */
public class DepartmentDAO {
    private final DatabaseManager dbManager;

    public DepartmentDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Create a new department
     */
    public boolean create(Department department) {
        String sql = "INSERT INTO Department (departmentName, location, budget, managerID) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, department.getDepartmentName());
            stmt.setString(2, department.getLocation());
            stmt.setBigDecimal(3, department.getBudget());
            if (department.getManagerID() != null) {
                stmt.setInt(4, department.getManagerID());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        department.setDepartmentID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get department by ID
     */
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

    /**
     * Get all departments
     */
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

    /**
     * Update department
     */
    public boolean update(Department department) {
        String sql = "UPDATE Department SET departmentName = ?, location = ?, budget = ?, managerID = ? WHERE departmentID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, department.getDepartmentName());
            stmt.setString(2, department.getLocation());
            stmt.setBigDecimal(3, department.getBudget());
            if (department.getManagerID() != null) {
                stmt.setInt(4, department.getManagerID());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            stmt.setInt(5, department.getDepartmentID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete department
     */
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

    /**
     * Extract Department object from ResultSet
     */
    private Department extractDepartmentFromResultSet(ResultSet rs) throws SQLException {
        Department dept = new Department();
        dept.setDepartmentID(rs.getInt("departmentID"));
        dept.setDepartmentName(rs.getString("departmentName"));
        dept.setLocation(rs.getString("location"));

        BigDecimal budget = rs.getBigDecimal("budget");
        dept.setBudget(budget);

        int managerID = rs.getInt("managerID");
        if (!rs.wasNull()) {
            dept.setManagerID(managerID);
        }

        return dept;
    }
}
