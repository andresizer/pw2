package com.finance.transaction.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Entidade Transaction - Representa uma transação financeira
 * Extende PanacheEntity para usar os métodos do Panache
 */
@Entity
@Table(name = "transactions")
public class Transaction extends PanacheEntity {
    
    /**
     * ID do usuário dono da transação
     * Referência ao User do auth-service (não é @ManyToOne pois está em outro banco)
     */
    @Column(nullable = false)
    public Long userId;
    
    /**
     * Descrição da transação
     */
    @Column(nullable = false, length = 255)
    public String description;
    
    /**
     * Valor da transação
     */
    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal amount;
    
    /**
     * Tipo da transação (INCOME ou EXPENSE)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    public TransactionType type;
    
    /**
     * Data da transação
     */
    @Column(nullable = false)
    public LocalDate date;
    
    /**
     * Enum para tipo de transação
     */
    public enum TransactionType {
        INCOME,  // Receita
        EXPENSE  // Despesa
    }
    
    /**
     * Construtor padrão (necessário para JPA)
     */
    public Transaction() {
    }
    
    /**
     * Construtor com parâmetros
     */
    public Transaction(Long userId, String description, BigDecimal amount, 
                      TransactionType type, LocalDate date) {
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.date = date;
    }
    
    /**
     * Busca todas as transações de um usuário
     * 
     * @param userId ID do usuário
     * @return Lista de transações
     */
    public static List<Transaction> findByUserId(Long userId) {
        return list("userId = ?1 order by date desc", userId);
    }
    
    /**
     * Busca uma transação específica de um usuário
     * 
     * @param id ID da transação
     * @param userId ID do usuário
     * @return Transação encontrada ou null
     */
    public static Transaction findByIdAndUserId(Long id, Long userId) {
        return find("id = ?1 and userId = ?2", id, userId).firstResult();
    }
    
    /**
     * Busca transações por tipo
     * 
     * @param userId ID do usuário
     * @param type Tipo da transação
     * @return Lista de transações
     */
    public static List<Transaction> findByUserIdAndType(Long userId, TransactionType type) {
        return list("userId = ?1 and type = ?2 order by date desc", userId, type);
    }
    
    /**
     * Busca transações por período
     * 
     * @param userId ID do usuário
     * @param startDate Data inicial
     * @param endDate Data final
     * @return Lista de transações
     */
    public static List<Transaction> findByUserIdAndPeriod(Long userId, 
                                                         LocalDate startDate, 
                                                         LocalDate endDate) {
        return list("userId = ?1 and date >= ?2 and date <= ?3 order by date desc", 
                   userId, startDate, endDate);
    }
    
    /**
     * Calcula o saldo total de um usuário
     * 
     * @param userId ID do usuário
     * @return Saldo (receitas - despesas)
     */
    public static BigDecimal calculateBalance(Long userId) {
        List<Transaction> transactions = findByUserId(userId);
        
        BigDecimal balance = BigDecimal.ZERO;
        for (Transaction t : transactions) {
            if (t.type == TransactionType.INCOME) {
                balance = balance.add(t.amount);
            } else {
                balance = balance.subtract(t.amount);
            }
        }
        
        return balance;
    }
    
    /**
     * Calcula total de receitas
     * 
     * @param userId ID do usuário
     * @return Total de receitas
     */
    public static BigDecimal calculateTotalIncome(Long userId) {
        List<Transaction> incomes = findByUserIdAndType(userId, TransactionType.INCOME);
        return incomes.stream()
                .map(t -> t.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Calcula total de despesas
     * 
     * @param userId ID do usuário
     * @return Total de despesas
     */
    public static BigDecimal calculateTotalExpense(Long userId) {
        List<Transaction> expenses = findByUserIdAndType(userId, TransactionType.EXPENSE);
        return expenses.stream()
                .map(t -> t.amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Conta total de transações de um usuário
     * 
     * @param userId ID do usuário
     * @return Número de transações
     */
    public static long countByUserId(Long userId) {
        return count("userId", userId);
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", userId=" + userId +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", date=" + date +
                '}';
    }
}