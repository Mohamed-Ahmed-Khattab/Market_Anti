package com.example.demo.model;

import com.example.demo.db.DatabaseManager;
import com.example.demo.util.DiscountedProduct;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manager entity representing management staff
 * Maps to the Manager table in the database
 */
public class Manager extends Employee {
    private int managerID;
    private String managementLevel;
    private BigDecimal bonus;

    private List<Employee> departmentEmployees;
    private Department department;

    public Manager() {
        super();
        this.departmentEmployees = new ArrayList<>();
    }

    // Conforming to Employee constructor
    public Manager(String name, String gender, String address, java.time.LocalDate dob, String password, double salary,
            String jobTitle,
            List<String> phoneNumbers) {
        super(name, gender, address, dob, password, salary, jobTitle, phoneNumbers);
        this.departmentEmployees = new ArrayList<>();
    }

    public String evaluateEmployee(Employee e) {
        // Placeholder logic
        return "Employee " + e.getName() + " evaluated.";
    }

    public void assignDepartment(Employee e) {
        if (this.department != null) {
            e.assignDepartment(this.department);
            if (!this.departmentEmployees.contains(e)) {
                this.departmentEmployees.add(e);
            }
        }
    }

    public void unAssignDepartment(Employee e) {
        if (this.departmentEmployees.contains(e)) {
            this.departmentEmployees.remove(e);
            e.assignDepartment(null);
        }
    }

    /**
     * Analyzes market trends to find the most sold product and returns it with a 5%
     * discount.
     * This discount is only applied in-memory and does NOT modify the database
     * price.
     *
     * @return DiscountedProduct containing the most sold product with 5% discount,
     *         or null if no sales data
     */
    public DiscountedProduct analyzeMarketTrends() {
        System.out.println("Analyzing market trends...");

        DatabaseManager dbManager = DatabaseManager.getInstance();

        // Query to find the product with highest total quantity sold from
        // completed/checked-out carts
        String sql = """
                SELECT p.productID, p.productName, p.price, SUM(ci.quantity) as totalSold
                FROM CartItem ci
                JOIN Cart c ON ci.cartID = c.cartID
                JOIN Product p ON ci.productID = p.productID
                WHERE c.status IN ('checked_out', 'completed')
                GROUP BY p.productID, p.productName, p.price
                ORDER BY totalSold DESC
                LIMIT 1
                """;

        try (Connection conn = dbManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int productID = rs.getInt("productID");
                String productName = rs.getString("productName");
                double originalPrice = rs.getDouble("price");
                int totalSold = rs.getInt("totalSold");

                System.out.println("Most sold product: " + productName + " (ID: " + productID +
                        ", Sold: " + totalSold + ", Price: $" + originalPrice + ")");

                // Return the discounted product (5% off is applied in DiscountedProduct
                // constructor)
                return new DiscountedProduct(productID, productName, originalPrice);
            }
        } catch (SQLException e) {
            System.err.println("Error analyzing market trends: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("No sales data found for market trend analysis.");
        return null;
    }

    public void assignTask(Employee e, String task) {
        System.out.println("Assigned task '" + task + "' to " + e.getName());
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public List<Employee> getDepartmentEmployees() {
        return departmentEmployees;
    }

    public void setDepartmentEmployees(List<Employee> employees) {
        this.departmentEmployees = employees;
    }

    public int getManagerID() {
        return managerID;
    }

    public void setManagerID(int managerID) {
        this.managerID = managerID;
    }

    public String getManagementLevel() {
        return managementLevel;
    }

    public void setManagementLevel(String managementLevel) {
        this.managementLevel = managementLevel;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }
}
