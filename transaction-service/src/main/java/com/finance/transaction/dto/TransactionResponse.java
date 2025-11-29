package com.finance.transaction.dto;

import com.finance.transaction.entity.Transaction;
import com.finance.transaction.entity.Transaction.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {
    public Long id;
    public Long userId;
    public String description;
    public BigDecimal amount;
    public TransactionType type;
    public LocalDate date;
    
    public TransactionResponse() {
    }
    
    public TransactionResponse(Transaction transaction) {
        this.id = transaction.id;
        this.userId = transaction.userId;
        this.description = transaction.description;
        this.amount = transaction.amount;
        this.type = transaction.type;
        this.date = transaction.date;
    }
    
    public TransactionResponse(Long id, Long userId, String description, 
                              BigDecimal amount, TransactionType type, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }
}