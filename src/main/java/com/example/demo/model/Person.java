package com.example.demo.model;
import java.time.LocalDate;
import java.time.Period;

public abstract class Person implements SearchItem {
    private String ssn; // readOnly
    private static int lastSSN = 1000;
    private String name;
    private String gender;
    private String address;
    private final LocalDate dateOfBirth; // readOnly

    public Person(String name, String gender, String address, LocalDate dob) {
        this.ssn = "SSN-" + (++lastSSN);
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.dateOfBirth = dob;
    }

    @Override
    public boolean matches(String keyword) {
        return name.toLowerCase().contains(keyword.toLowerCase());
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', ssn='" + ssn + "'}";
    }

    public abstract String getRole();

    public int calculateAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    // Getters and Setters
    public String getSsn() { return ssn; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocalDate getDob() { return dateOfBirth; }
}
