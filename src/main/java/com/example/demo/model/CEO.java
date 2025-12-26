package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class CEO {
    private static CEO uniqueInstance;
    private List<Manager> departmentManager = new ArrayList<>();
    private static List<String> ceoLog = new ArrayList<>();

    private CEO() {
    }

    public static synchronized CEO getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new CEO();
        }
        return uniqueInstance;
    }

    public void checkSuppliers() {
        System.out.println("Checking all supplier statuses...");
    }

    public void approveBudget() {
        System.out.println("Budget Approved.");
    }

    public void setInfo(String companyVision) {
        System.out.println("Company Vision set to: " + companyVision);
    }

    public void promoteToManager(String ssn, Department dept) {
        System.out.println("Promoting employee " + ssn + " to manager of " + dept.getDepartmentName());
    }

    // Getters and Setters
    public List<Manager> getDepartmentManager() {
        return departmentManager;
    }

    public void setDepartmentManager(List<Manager> departmentManager) {
        this.departmentManager = departmentManager;
    }
}
