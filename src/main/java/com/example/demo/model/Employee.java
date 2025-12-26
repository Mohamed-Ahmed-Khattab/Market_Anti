package com.example.demo.model;

/**
 * Employee entity representing staff members
 * Maps to the Employee table in the database
 */
public class Employee extends Person {
    private double salary;
    protected String jobTitle;
    private java.util.List<String> phoneNumbers;

    public Employee(String name, String gender, String address, java.time.LocalDate dob, double salary, String jobTitle,
            java.util.List<String> phoneNumbers) {
        super(name, gender, address, dob);
        this.salary = salary;
        this.jobTitle = jobTitle;
        this.phoneNumbers = phoneNumbers;
    }

    public void assignDepartment(Department d) {
        // Association logic to be refined if bidirectional
        if (d != null) {
            d.addEmployee(this);
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

    public java.util.List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(java.util.List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
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
                '}';
    }
}
