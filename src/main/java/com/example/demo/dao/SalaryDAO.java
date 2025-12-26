package com.example.demo.dao;

import com.example.demo.db.DatabaseManager;
import com.example.demo.model.Salary;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Salary entity
 */
public class SalaryDAO {
    private final DatabaseManager dbManager;

    public SalaryDAO() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public boolean create(Salary salary) {
        String sql = "INSERT INTO Salary (employeeID, baseSalary, bonus, deductions, effectiveDate, endDate, paymentFrequency) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, salary.getEmployeeID());
            stmt.setBigDecimal(2, salary.getBaseSalary());
            stmt.setBigDecimal(3, salary.getBonus());
            stmt.setBigDecimal(4, salary.getDeductions());
            stmt.setDate(5, Date.valueOf(salary.getEffectiveDate()));

            if (salary.getEndDate() != null) {
                stmt.setDate(6, Date.valueOf(salary.getEndDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, salary.getPaymentFrequency());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        salary.setSalaryID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Salary getById(int salaryID) {
        String sql = "SELECT * FROM Salary WHERE salaryID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaryID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractSalaryFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Salary getCurrentSalaryByEmployee(int employeeID) {
        String sql = "SELECT * FROM Salary WHERE employeeID = ? AND endDate IS NULL ORDER BY effectiveDate DESC LIMIT 1";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractSalaryFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Salary> getByEmployee(int employeeID) {
        List<Salary> salaries = new ArrayList<>();
        String sql = "SELECT * FROM Salary WHERE employeeID = ? ORDER BY effectiveDate DESC";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, employeeID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                salaries.add(extractSalaryFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaries;
    }

    public boolean update(Salary salary) {
        String sql = "UPDATE Salary SET employeeID = ?, baseSalary = ?, bonus = ?, deductions = ?, effectiveDate = ?, endDate = ?, paymentFrequency = ? WHERE salaryID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salary.getEmployeeID());
            stmt.setBigDecimal(2, salary.getBaseSalary());
            stmt.setBigDecimal(3, salary.getBonus());
            stmt.setBigDecimal(4, salary.getDeductions());
            stmt.setDate(5, Date.valueOf(salary.getEffectiveDate()));

            if (salary.getEndDate() != null) {
                stmt.setDate(6, Date.valueOf(salary.getEndDate()));
            } else {
                stmt.setNull(6, Types.DATE);
            }

            stmt.setString(7, salary.getPaymentFrequency());
            stmt.setInt(8, salary.getSalaryID());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int salaryID) {
        String sql = "DELETE FROM Salary WHERE salaryID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, salaryID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean endSalary(int salaryID, LocalDate endDate) {
        String sql = "UPDATE Salary SET endDate = ? WHERE salaryID = ?";

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(endDate));
            stmt.setInt(2, salaryID);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Salary extractSalaryFromResultSet(ResultSet rs) throws SQLException {
        Salary salary = new Salary();
        salary.setSalaryID(rs.getInt("salaryID"));
        salary.setEmployeeID(rs.getInt("employeeID"));
        salary.setBaseSalary(rs.getBigDecimal("baseSalary"));
        salary.setBonus(rs.getBigDecimal("bonus"));
        salary.setDeductions(rs.getBigDecimal("deductions"));

        Date effectiveDate = rs.getDate("effectiveDate");
        if (effectiveDate != null) {
            salary.setEffectiveDate(effectiveDate.toLocalDate());
        }

        Date endDate = rs.getDate("endDate");
        if (endDate != null) {
            salary.setEndDate(endDate.toLocalDate());
        }

        salary.setPaymentFrequency(rs.getString("paymentFrequency"));

        return salary;
    }
}
