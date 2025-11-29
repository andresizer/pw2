package com.finance.transaction.dto;

import com.finance.transaction.entity.Transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionRequest {
    public String description;
    public BigDecimal amount;
    public TransactionType type;
    public LocalDate date;
    
    public TransactionRequest() {
    }
    
    public TransactionRequest(String description, BigDecimal amount, 
                             TransactionType type, LocalDate date) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }
}