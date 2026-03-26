package com.democlass.pos.dto;

import java.math.BigDecimal;

public class CashSummaryDTO {
    private BigDecimal totalSales;
    private BigDecimal totalRefunds;
    private BigDecimal balance;

    // Constructors
    public CashSummaryDTO() {}

    public CashSummaryDTO(BigDecimal totalSales, BigDecimal totalRefunds, BigDecimal balance) {
        this.totalSales = totalSales;
        this.totalRefunds = totalRefunds;
        this.balance = balance;
    }

    // Getters and Setters
    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public BigDecimal getTotalRefunds() {
        return totalRefunds;
    }

    public void setTotalRefunds(BigDecimal totalRefunds) {
        this.totalRefunds = totalRefunds;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
