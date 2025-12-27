package com.example.demo.model;

import java.time.LocalDate;
import java.time.Period;

public abstract class Person implements SearchItem {
    private String ssn; // readOnly
    private static int lastSSN = 1000;
    private String name;
    private String gender;
    private String address;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth; // readOnly
    private String password;

    public Person(String name, String gender, String address, LocalDate dob) {
        this.ssn = "SSN-" + (++lastSSN);
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.dateOfBirth = dob;
    }

    public Person(String name, String gender, String address, LocalDate dob, String password) {
        this(name, gender, address, dob);
        this.password = password;
    }

    protected Person() {
        // Default constructor for subclasses
    }

    public abstract String getRole();

    @Override
    public boolean matches(String keyword) {
        if (keyword == null)
            return false;
        return name.toLowerCase().contains(keyword.toLowerCase());
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', ssn='" + ssn + "'}";
    }

    public int calculateAge() {
        if (dateOfBirth == null)
            return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    public String getSsn() {
        return ssn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDob() {
        return dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
    }
}
