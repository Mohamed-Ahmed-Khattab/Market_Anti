package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Employee entity representing staff members
 * Maps to the Employee table in the database
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Employee implements SearchItem {
    private Integer employeeID;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    private Integer departmentID;
    private String position;
    private Boolean isManager;

    // Associations
    private double salary; // Derived/Associated logic
    private java.util.List<String> phoneNumbers = new java.util.ArrayList<>();

    // UML Methods
    public void assignDepartment(Department d) {
        this.departmentID = (d != null) ? d.getDepartmentID() : null;
    }

    public void newSalary(double salary) {
        this.salary = salary;
    }

    public void serveCustomer(Customer c) {
        System.out.println("Serving customer: " + c.getFullName());
        c.setAssignedEmployee(this);
    }

    public void setJobTitle(String title) {
        this.position = title;
    }

    public String getJobTitle() {
        return position;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public java.util.List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(java.util.List<String> phones) {
        this.phoneNumbers = phones;
    }

    // Constructor without ID for new employees
    public Employee(String firstName, String lastName, String email, String phoneNumber,
            LocalDate hireDate, Integer departmentID, String position, Boolean isManager) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.hireDate = hireDate;
        this.departmentID = departmentID;
        this.position = position;
        this.isManager = isManager;
    }

    /**
     * Get full name of employee
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean matches(String keyword) {
        if (keyword == null || keyword.isEmpty())
            return true;
        String lowerKey = keyword.toLowerCase();
        return (firstName != null && firstName.toLowerCase().contains(lowerKey)) ||
                (lastName != null && lastName.toLowerCase().contains(lowerKey)) ||
                (email != null && email.toLowerCase().contains(lowerKey)) ||
                (position != null && position.toLowerCase().contains(lowerKey));
    }
}
