package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Manager entity representing management staff
 * Maps to the Manager table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Manager {
    private Integer managerID;
    private Integer employeeID;
    private Integer departmentID;
    private String managementLevel;
    private BigDecimal bonus;

    private Department department;
    private java.util.List<Employee> departmentEmployees = new java.util.ArrayList<>();

    // Business Methods from UML
    public String evaluateEmployee(Employee e) {
        return "Employee " + e.getFullName() + " evaluated.";
    }

    public void assignDepartment(Department d) {
        this.department = d;
        this.departmentID = d.getDepartmentID();
    }

    public void unAssignDepartment() {
        this.department = null;
        this.departmentID = null;
    }

    public void analyzeMarketTrends() {
        System.out.println("Analyzing market trends...");
    }

    public void assignTask(Employee e, String task) {
        System.out.println("Assigned task '" + task + "' to " + e.getFullName());
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
        if (department != null)
            this.departmentID = department.getDepartmentID();
    }

    public java.util.List<Employee> getDepartmentEmployees() {
        return departmentEmployees;
    }

    public void setDepartmentEmployees(java.util.List<Employee> employees) {
        this.departmentEmployees = employees;
    }

    // Constructor without ID for new managers
    public Manager(Integer employeeID, Integer departmentID, String managementLevel, BigDecimal bonus) {
        this.employeeID = employeeID;
        this.departmentID = departmentID;
        this.managementLevel = managementLevel;
        this.bonus = bonus;
    }
}
