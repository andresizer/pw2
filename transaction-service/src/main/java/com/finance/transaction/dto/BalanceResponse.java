package com.finance.transaction.dto;

import java.math.BigDecimal;

public class BalanceResponse {
    public BigDecimal balance;
    public BigDecimal totalIncome;
    public BigDecimal totalExpense;
    public Long userId;
    public Long transactionCount;
    
    public BalanceResponse() {
    }
    
    public BalanceResponse(BigDecimal balance, Long userId) {
        this.balance = balance;
        this.userId = userId;
    }
    
    public BalanceResponse(BigDecimal balance, BigDecimal totalIncome, 
                          BigDecimal totalExpense, Long userId, Long transactionCount) {
        this.balance = balance;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.userId = userId;
        this.transactionCount = transactionCount;
    }
}