package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Department entity representing organizational units
 * Maps to the Department table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Department implements SearchItem {
    private Integer departmentID;
    private String departmentName;
    private String location;
    private BigDecimal budget;
    private Integer managerID;

    // Associations from UML
    private Manager manager;
    private java.util.List<Employee> employees = new java.util.ArrayList<>();

    // Business Methods from UML
    public double calculateTotalSalaries() {
        // Placeholder implementation - in real app would sum employee salaries
        return 0.0;
    }

    public String getManagerName() {
        return (manager != null) ? "Manager ID: " + manager.getManagerID() : "No Manager";
    }

    public void addEmployee(Employee e) {
        if (!employees.contains(e)) {
            employees.add(e);
        }
    }

    public void setManager(Manager m) {
        this.manager = m;
        this.managerID = (m != null) ? m.getManagerID() : null;
    }

    public int getEmployeeCount() {
        return employees.size();
    }

    public void setBudget(double budget) {
        this.budget = BigDecimal.valueOf(budget);
    }

    public double getBudget() {
        return (budget != null) ? budget.doubleValue() : 0.0;
    }

    // Constructor without ID for new departments
    public Department(String departmentName, String location, BigDecimal budget, Integer managerID) {
        this.departmentName = departmentName;
        this.location = location;
        this.budget = budget;
        this.managerID = managerID;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isEmpty())
            return true;
        String lowerKey = keyword.toLowerCase();
        return (departmentName != null && departmentName.toLowerCase().contains(lowerKey)) ||
                (location != null && location.toLowerCase().contains(lowerKey));
    }
}
