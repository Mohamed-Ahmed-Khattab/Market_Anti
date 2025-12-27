package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Employee entity
 * Handles all database operations for employees
 */
public class EmployeeDAO {
    private final DatabaseManager dbManager;

    public EmployeeDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Create a new employee
     */
    public boolean create(Employee employee) {
        String sql = "INSERT INTO Employee (firstName, lastName, email, phoneNumber, hireDate, departmentID, position, isManager, password) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhoneNumber());
            stmt.setDate(5, Date.valueOf(employee.getHireDate()));

            if (employee.getDepartmentID() != null) {
                stmt.setInt(6, employee.getDepartmentID());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, employee.getPosition());
            stmt.setBoolean(8, employee.getIsManager() != null ? employee.getIsManager() : false);
            stmt.setString(9, employee.getPassword());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        employee.setEmployeeID(generatedKeys.getInt(1));
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
     * Get employee by ID
     */
    public Employee getById(int employeeID) {
        String sql = "SELECT * FROM Employee WHERE employeeID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractEmployeeFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all employees
     */
    public List<Employee> getAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee ORDER BY lastName, firstName";

        try (Connection conn = dbManager.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * Get employees by department
     */
    public List<Employee> getByDepartment(int departmentID) {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee WHERE departmentID = ? ORDER BY lastName, firstName";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, departmentID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                employees.add(extractEmployeeFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    /**
     * Update employee
     */
    public boolean update(Employee employee) {
        String sql = "UPDATE Employee SET firstName = ?, lastName = ?, email = ?, phoneNumber = ?, hireDate = ?, departmentID = ?, position = ?, isManager = ?, password = ? WHERE employeeID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employee.getFirstName());
            stmt.setString(2, employee.getLastName());
            stmt.setString(3, employee.getEmail());
            stmt.setString(4, employee.getPhoneNumber());
            stmt.setDate(5, Date.valueOf(employee.getHireDate()));

            if (employee.getDepartmentID() != null) {
                stmt.setInt(6, employee.getDepartmentID());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, employee.getPosition());
            stmt.setBoolean(8, employee.getIsManager() != null ? employee.getIsManager() : false);
            stmt.setString(9, employee.getPassword());
            stmt.setInt(10, employee.getEmployeeID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete employee
     */
    public boolean delete(int employeeID) {
        String sql = "DELETE FROM Employee WHERE employeeID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Extract Employee object from ResultSet
     */
    private Employee extractEmployeeFromResultSet(ResultSet rs) throws SQLException {
        Employee emp = new Employee();
        emp.setEmployeeID(rs.getInt("employeeID"));
        emp.setFirstName(rs.getString("firstName"));
        emp.setLastName(rs.getString("lastName"));
        emp.setEmail(rs.getString("email"));
        emp.setPhoneNumber(rs.getString("phoneNumber"));

        Date hireDate = rs.getDate("hireDate");
        if (hireDate != null) {
            emp.setHireDate(hireDate.toLocalDate());
        }

        int deptID = rs.getInt("departmentID");
        if (!rs.wasNull()) {
            emp.setDepartmentID(deptID);
        }

        emp.setPosition(rs.getString("position"));
        emp.setIsManager(rs.getBoolean("isManager"));
        emp.setPassword(rs.getString("password"));

        return emp;
    }
}
