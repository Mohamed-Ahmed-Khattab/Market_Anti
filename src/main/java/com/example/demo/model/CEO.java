package com.example.demo.model;

import java.util.List;

public class CEO extends Manager {
    private static CEO uniqueInstance;
    private java.util.List<Manager> departmentManager;

    private CEO(String name, String gender, String address, java.time.LocalDate dob, String password, double salary,
            String jobTitle,
            java.util.List<String> phoneNumbers) {
        super(name, gender, address, dob, password, salary, jobTitle, phoneNumbers);
        this.departmentManager = new java.util.ArrayList<>();
    }

    public static CEO getInstance(String name, String gender, String address, java.time.LocalDate dob, String password,
            double salary,
            String jobTitle, java.util.List<String> phoneNumbers) {
        if (uniqueInstance == null) {
            uniqueInstance = new CEO(name, gender, address, dob, password, salary, jobTitle, phoneNumbers);
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

    public static java.util.List<Supplier> checkSuppliers() {
        com.example.demo.dao.SupplierDAO supplierDAO = new com.example.demo.dao.SupplierDAO();
        java.util.List<Supplier> allSuppliers = supplierDAO.getAll();
        java.util.List<Supplier> highRatedSuppliers = new java.util.ArrayList<>();

        for (Supplier s : allSuppliers) {
            if (s.getRating() > 3) {
                highRatedSuppliers.add(s);
            }
        }
        return highRatedSuppliers;
    }

    public static boolean approveBudget(double requiredAmount) {
        System.out.println("Approving budget...");
        com.example.demo.dao.CartDAO cartDAO = new com.example.demo.dao.CartDAO();
        double totalSales = 0;

        for (int i = 0; i <= 30; i++) {
            java.time.LocalDate date = java.time.LocalDate.now().minusDays(i);
            totalSales += cartDAO.getTotalSalesRevenue(date);
            if (totalSales >= requiredAmount) {
                return true;
            }
        }
        return false;
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

    public static boolean promoteEmployee(Employee e) {
        String currentPos = e.getPosition() != null ? e.getPosition() : "Entry Level";
        String nextPos = "";
        boolean becomingManager = false;

        switch (currentPos) {
            case "Entry Level":
                nextPos = "Junior";
                break;

            case "CEO":
                nextPos = "CEO";
                break;
            case "Junior":
                nextPos = "Senior";
                break;
            case "Senior":
                nextPos = "Manager";
                becomingManager = true;
                break;
            case "Manager":
                com.example.demo.util.AlertUtil.showWarning("Promotion Limit",
                        "Employee is already at the highest level.");
                return false;
            default:
                nextPos = "Junior"; // Default promotion path
                break;
        }

        if (becomingManager) {
            // Check constraint: No more than 1 manager per department
            if (e.getDepartmentID() != null) {
                com.example.demo.dao.EmployeeDAO empDAO = new com.example.demo.dao.EmployeeDAO();
                java.util.List<Employee> deptEmployees = empDAO.getByDepartment(e.getDepartmentID());
                for (Employee deptEmp : deptEmployees) {
                    if (Boolean.TRUE.equals(deptEmp.getIsManager())) {
                        com.example.demo.util.AlertUtil.showWarning("Promotion Blocked",
                                "Manager cant have 2 managers in one department");
                        return false;
                    }
                }
            }

            // Promote to Manager
            e.setPosition(nextPos);
            e.setIsManager(true);

            com.example.demo.dao.EmployeeDAO employeeDAO = new com.example.demo.dao.EmployeeDAO();
            if (employeeDAO.update(e)) {
                com.example.demo.dao.ManagerDAO managerDAO = new com.example.demo.dao.ManagerDAO();
                com.example.demo.model.Manager newManager = new com.example.demo.model.Manager();
                newManager.setEmployeeID(e.getEmployeeID());
                newManager.setDepartmentID(e.getDepartmentID());
                newManager.setManagementLevel("Level 1");
                newManager.setBonus(java.math.BigDecimal.ZERO);
                managerDAO.create(newManager);
                return true;
            }
        } else {
            // Normal Promotion
            e.setPosition(nextPos);
            com.example.demo.dao.EmployeeDAO employeeDAO = new com.example.demo.dao.EmployeeDAO();
            return employeeDAO.update(e);
        }
        return false;
    }

    public static boolean demoteEmployee(Employee e) {
        String currentPos = e.getPosition() != null ? e.getPosition() : "Entry Level";
        String prevPos = "";
        boolean strippingManager = false;

        switch (currentPos) {
            case "Manager":
                prevPos = "Senior";
                strippingManager = true;
                break;
            case "CEO":
                prevPos = "CEO";
                break;
            case "Senior":
                prevPos = "Junior";
                break;
            case "Junior":
                prevPos = "Entry Level";
                break;
            case "Entry Level":
                com.example.demo.util.AlertUtil.showWarning("Demotion Limit",
                        "Employee is already at the lowest level.");
                return false;
            default:
                prevPos = "Entry Level";
                break;
        }

        if (strippingManager) {
            // Demote from Manager
            com.example.demo.dao.ManagerDAO managerDAO = new com.example.demo.dao.ManagerDAO();
            com.example.demo.model.Manager mgr = managerDAO.getByEmployeeId(e.getEmployeeID());
            if (mgr != null) {
                managerDAO.delete(mgr.getManagerID());
            }

            e.setIsManager(false);
            e.setPosition(prevPos);

            com.example.demo.dao.EmployeeDAO employeeDAO = new com.example.demo.dao.EmployeeDAO();
            return employeeDAO.update(e);
        } else {
            // Normal Demotion
            e.setPosition(prevPos);
            com.example.demo.dao.EmployeeDAO employeeDAO = new com.example.demo.dao.EmployeeDAO();
            return employeeDAO.update(e);
        }
    }
}
