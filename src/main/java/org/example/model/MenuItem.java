package org.example.model;

public class MenuItem {
    private Long id;
    private String itemCode;
    private String name;
    private Double basePrice;
    private String status;
    private String description;

    public MenuItem() {
    }

    public MenuItem(Long id, String itemCode, String name, Double basePrice, String status) {
        this.id = id;
        this.itemCode = itemCode;
        this.name = name;
        this.basePrice = basePrice;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%s - $%.2f (%s)", name, basePrice, status);
    }
}


