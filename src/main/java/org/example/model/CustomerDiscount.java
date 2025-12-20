package org.example.model;

import java.math.BigDecimal;
import java.sql.Date;

public class CustomerDiscount {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String middleName;
    private BigDecimal discountValue;
    private String discountType;
    private Date validFrom;
    private Date validTo;

    public CustomerDiscount() {
    }

    public CustomerDiscount(Long customerId, String firstName, String lastName, String middleName, 
                           BigDecimal discountValue, String discountType, Date validFrom, Date validTo) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
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

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(BigDecimal discountValue) {
        this.discountValue = discountValue;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getValidTo() {
        return validTo;
    }

    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

    @Override
    public String toString() {
        String name = firstName + " " + (middleName != null ? middleName + " " : "") + lastName;
        String validPeriod = validTo != null ? 
            validFrom.toString() + " - " + validTo.toString() : 
            "from " + validFrom.toString();
        return String.format("Customer ID: %d, Name: %s, Discount: %s %s, Type: %s, Valid: %s", 
            customerId, name, discountValue, discountType, discountType, validPeriod);
    }
}
