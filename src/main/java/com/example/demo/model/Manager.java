package com.example.demo.model;

/**
 * Manager entity representing management staff
 * Maps to the Manager table in the database
 */
public class Manager extends Employee {
    private java.util.List<Employee> departmentEmployees;
    private Department department;

    // Conforming to Employee constructor
    public Manager(String name, String gender, String address, java.time.LocalDate dob, double salary, String jobTitle,
            java.util.List<String> phoneNumbers) {
        super(name, gender, address, dob, salary, jobTitle, phoneNumbers);
        this.departmentEmployees = new java.util.ArrayList<>();
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

    public java.util.List<Employee> getDepartmentEmployees() {
        return departmentEmployees;
    }

    public void setDepartmentEmployees(java.util.List<Employee> employees) {
        this.departmentEmployees = employees;
    }

}
