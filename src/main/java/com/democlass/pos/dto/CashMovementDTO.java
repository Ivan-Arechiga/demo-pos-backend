package com.democlass.pos.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CashMovementDTO {
    private Long id;
    private String type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime date;
    private Long referenceId;
    private LocalDateTime createdAt;

    // Constructors
    public CashMovementDTO() {}

    public CashMovementDTO(Long id, String type, BigDecimal amount, String description, LocalDateTime date, Long referenceId, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.referenceId = referenceId;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
