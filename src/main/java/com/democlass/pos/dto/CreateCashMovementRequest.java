package com.democlass.pos.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public class CreateCashMovementRequest {
    @NotBlank(message = "Movement type is required")
    private String type;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    private String description;

    private Long referenceId;

    // Constructors
    public CreateCashMovementRequest() {}

    public CreateCashMovementRequest(String type, BigDecimal amount, String description, Long referenceId) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.referenceId = referenceId;
    }

    // Getters and Setters
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

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }
}
