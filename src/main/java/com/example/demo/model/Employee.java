package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Employee entity representing staff members
 * Maps to the Employee table in the database
 */
public class Employee extends Person {
    private int employeeID;
    private double salary;
    protected String jobTitle;
    private List<String> phoneNumbers;

    // Fields for DAO compatibility
    private String email;
    private String phoneNumber;
    private LocalDate hireDate;
    private Integer departmentID;
    private Boolean isManager;

    public Employee() {
        super();
        this.phoneNumbers = new ArrayList<>();
    }

    public Employee(String name, String gender, String address, LocalDate dob, String password, double salary,
            String jobTitle,
            List<String> phoneNumbers) {
        super(name, gender, address, dob, password);
        this.salary = salary;
        this.jobTitle = jobTitle;
        this.phoneNumbers = phoneNumbers;
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            this.phoneNumber = phoneNumbers.get(0);
        }
    }

    public void assignDepartment(Department d) {
        // Association logic to be refined if bidirectional
        if (d != null) {
            d.addEmployee(this);
            this.departmentID = d.getId(); // Assuming Department has getId()
        }
    }

    public void newSalary(double salary) {
        this.salary = salary;
    }

    public void serveCustomer(Customer c) {
        if (c != null) {
            c.setAssignedEmployee(this);
        }
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            this.phoneNumber = phoneNumbers.get(0);
        }
    }

    // DAO Compatibility Methods

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        if (this.phoneNumbers == null) {
            this.phoneNumbers = new ArrayList<>();
        }
        if (phoneNumber != null && !this.phoneNumbers.contains(phoneNumber)) {
            this.phoneNumbers.add(phoneNumber);
        }
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public Integer getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(Integer departmentID) {
        this.departmentID = departmentID;
    }

    public String getPosition() {
        return jobTitle;
    }

    public void setPosition(String position) {
        this.jobTitle = position;
    }

    public Boolean getIsManager() {
        return isManager;
    }

    public void setIsManager(Boolean isManager) {
        this.isManager = isManager;
    }

    public String getFirstName() {
        String name = getName();
        if (name == null)
            return "";
        String[] parts = name.split(" ", 2);
        return parts[0];
    }

    public void setFirstName(String firstName) {
        String lastName = getLastName();
        setName(firstName + (lastName.isEmpty() ? "" : " " + lastName));
    }

    public String getLastName() {
        String name = getName();
        if (name == null)
            return "";
        String[] parts = name.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    public void setLastName(String lastName) {
        String firstName = getFirstName();
        setName(firstName + (lastName.isEmpty() ? "" : " " + lastName));
    }

    public String getFullName() {
        return getName();
    }

    public String getDepartmentName() {
        // This would require fetching the department if it's lazy loaded, or just
        // returning a placeholder if not set
        // Since we have departmentID mostly, and maybe Department object in Manager.
        // But Employee has just departmentID integer for DAO mostly.
        // Let's assume we can try to fetch it or return "Unknown"
        return "Department " + (departmentID != null ? departmentID : "N/A");
    }

    @Override
    public String getRole() {
        return "Employee";
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + getName() + '\'' +
                ", ssn='" + getSsn() + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", id=" + employeeID +
                '}';
    }
}
