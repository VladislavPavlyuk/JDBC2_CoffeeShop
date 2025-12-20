package org.example.model;

import java.sql.Date;

public class Customer {
    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private Date dateOfBirth;

    public Customer() {
    }

    public Customer(Long id, String firstName, String lastName, String middleName, Date dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.dateOfBirth = dateOfBirth;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String toString() {
        String name = firstName + " " + (middleName != null ? middleName + " " : "") + lastName;
        return String.format("Customer ID: %d, Name: %s, Date of Birth: %s", 
            id, name, dateOfBirth != null ? dateOfBirth.toString() : "N/A");
    }
}

