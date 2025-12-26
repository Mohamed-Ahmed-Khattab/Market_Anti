package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Manager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Manager entity
 */
public class ManagerDAO {
    private final DatabaseManager dbManager;

    public ManagerDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Manager manager) {
        String sql = "INSERT INTO Manager (managerID, employeeID, departmentID, managementLevel, bonus) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, manager.getManagerID());
            stmt.setInt(2, manager.getEmployeeID());

            if (manager.getDepartmentID() != null) {
                stmt.setInt(3, manager.getDepartmentID());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }

            stmt.setString(4, manager.getManagementLevel());
            stmt.setBigDecimal(5, manager.getBonus());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Manager getById(int managerID) {
        String sql = "SELECT * FROM Manager WHERE managerID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, managerID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractManagerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Manager getByEmployeeId(int employeeID) {
        String sql = "SELECT * FROM Manager WHERE employeeID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractManagerFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Manager> getAll() {
        List<Manager> managers = new ArrayList<>();
        String sql = "SELECT * FROM Manager ORDER BY managerID";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                managers.add(extractManagerFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return managers;
    }

    public boolean update(Manager manager) {
        String sql = "UPDATE Manager SET employeeID = ?, departmentID = ?, managementLevel = ?, bonus = ? WHERE managerID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, manager.getEmployeeID());

            if (manager.getDepartmentID() != null) {
                stmt.setInt(2, manager.getDepartmentID());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, manager.getManagementLevel());
            stmt.setBigDecimal(4, manager.getBonus());
            stmt.setInt(5, manager.getManagerID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int managerID) {
        String sql = "DELETE FROM Manager WHERE managerID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, managerID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Manager extractManagerFromResultSet(ResultSet rs) throws SQLException {
        Manager manager = new Manager();
        manager.setManagerID(rs.getInt("managerID"));
        manager.setEmployeeID(rs.getInt("employeeID"));

        int deptID = rs.getInt("departmentID");
        if (!rs.wasNull()) {
            manager.setDepartmentID(deptID);
        }

        manager.setManagementLevel(rs.getString("managementLevel"));
        manager.setBonus(rs.getBigDecimal("bonus"));

        return manager;
    }
}
