package com.example.demo.model;

import java.math.BigDecimal;
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
    public Manager(String name, String gender, String address, java.time.LocalDate dob, double salary, String jobTitle,
            List<String> phoneNumbers) {
        super(name, gender, address, dob, salary, jobTitle, phoneNumbers);
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

    public void analyzeMarketTrends() {
        System.out.println("Analyzing market trends...");
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
