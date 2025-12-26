package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class Department {
    private List<Employee> employees;
    private Manager manager; // Manager Manager
    private String name;
    private double budget;
    private String status;
    private double revenue;
    private List<String> locations;

    public Department(double budget, String name, String status) {
        this.budget = budget;
        this.name = name;
        this.status = status;
        this.employees = new ArrayList<>();
        this.locations = new ArrayList<>();
    }

    public double calculateTotalSalaries() {
        double total = 0;
        if (employees != null) {
            for (Employee e : employees) {
                total += e.getSalary();
            }
        }
        return total;
    }

    public String getManagerName() {
        return manager != null ? manager.getName() : "None";
    }

    public void addEmployee(Employee e) {
        if (!employees.contains(e)) {
            employees.add(e);
            e.assignDepartment(this);
        }
    }

    public int getEmployeeCount() {
        return employees != null ? employees.size() : 0;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    // Getters and Setters matching UML
    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public Manager getManager() {
        return manager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getBudget() {
        return budget;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setLocation(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getLocation() {
        return locations;
    }
}
