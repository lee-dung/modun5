package com.example.modun5.expense.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import  lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatDTO {
    private LocalDate date;
    private BigDecimal totalExpense;
    private BigDecimal totalIncome;
}
