package com.example.demo.model;

import java.util.List;

public class CEO extends Manager {
    private static CEO uniqueInstance;
    private java.util.List<Manager> departmentManager;

    private CEO(String name, String gender, String address, java.time.LocalDate dob, double salary, String jobTitle,
            java.util.List<String> phoneNumbers) {
        super(name, gender, address, dob, salary, jobTitle, phoneNumbers);
        this.departmentManager = new java.util.ArrayList<>();
    }

    public static CEO getInstance(String name, String gender, String address, java.time.LocalDate dob, double salary,
            String jobTitle, java.util.List<String> phoneNumbers) {
        if (uniqueInstance == null) {
            uniqueInstance = new CEO(name, gender, address, dob, salary, jobTitle, phoneNumbers);
        }
        return uniqueInstance;
    }

    // Allow getting instance without params if already created, or handle
    // initialization logic better
    // For simplicity based on UML `getInstance(): Manager` (which returns CEO
    // actually)
    public static CEO getInstance() {
        return uniqueInstance;
    }

    public void checkSuppliers() {
        System.out.println("Checking suppliers...");
    }

    public void approveBudget() {
        System.out.println("Approving budget...");
    }

    public void setInfo(String vision) {
        System.out.println("Setting company vision: " + vision);
        // Logic to store vision
    }

    public void promoteToManager(String ssn, Department d) {
        System.out.println("Promoting employee with SSN " + ssn + " to Manager of " + d.getName());
        // Logic to promote
    }

    public java.util.List<Manager> getDepartmentManager() {
        return departmentManager;
    }

    public void setDepartmentManager(List<Manager> departmentManager) {
        this.departmentManager = departmentManager;
    }
}
