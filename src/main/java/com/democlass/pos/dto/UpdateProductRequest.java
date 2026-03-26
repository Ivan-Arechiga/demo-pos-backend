package com.democlass.pos.dto;

import java.math.BigDecimal;

public class UpdateProductRequest {
    private String name;
    private String sku;
    private BigDecimal price;
    private Integer stock;
    private String status;

    // Constructors
    public UpdateProductRequest() {}

    public UpdateProductRequest(String name, String sku, BigDecimal price, Integer stock, String status) {
        this.name = name;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
        this.status = status;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
