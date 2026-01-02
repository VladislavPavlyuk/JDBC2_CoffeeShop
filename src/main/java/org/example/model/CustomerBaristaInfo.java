package org.example.model;

import java.sql.Date;

public class CustomerBaristaInfo {
    private Long customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerMiddleName;
    private Date customerDateOfBirth;
    private Long baristaId;
    private String baristaFirstName;
    private String baristaLastName;
    private Long orderId;
    private String orderNumber;

    public CustomerBaristaInfo() {
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerMiddleName() {
        return customerMiddleName;
    }

    public void setCustomerMiddleName(String customerMiddleName) {
        this.customerMiddleName = customerMiddleName;
    }

    public Date getCustomerDateOfBirth() {
        return customerDateOfBirth;
    }

    public void setCustomerDateOfBirth(Date customerDateOfBirth) {
        this.customerDateOfBirth = customerDateOfBirth;
    }

    public Long getBaristaId() {
        return baristaId;
    }

    public void setBaristaId(Long baristaId) {
        this.baristaId = baristaId;
    }

    public String getBaristaFirstName() {
        return baristaFirstName;
    }

    public void setBaristaFirstName(String baristaFirstName) {
        this.baristaFirstName = baristaFirstName;
    }

    public String getBaristaLastName() {
        return baristaLastName;
    }

    public void setBaristaLastName(String baristaLastName) {
        this.baristaLastName = baristaLastName;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public String toString() {
        String customerName = customerFirstName + " " + 
            (customerMiddleName != null ? customerMiddleName + " " : "") + 
            customerLastName;
        String baristaName = baristaFirstName + " " + baristaLastName;
        return String.format("Customer: %s (ID: %d, DOB: %s), Barista: %s (ID: %d), Order: %s (ID: %d)",
            customerName, customerId, 
            customerDateOfBirth != null ? customerDateOfBirth.toString() : "N/A",
            baristaName, baristaId, orderNumber, orderId);
    }
}








