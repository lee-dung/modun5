package com.example.modun5.expense.app.model;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    public enum Type {EXPENSE, INCOME}

    private Long id;
    private Long userId;
    private  Long walletId;
    private Long categoryId;
    private BigDecimal amount;
    private Type type;
    private String note;
    private LocalDate transactionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}