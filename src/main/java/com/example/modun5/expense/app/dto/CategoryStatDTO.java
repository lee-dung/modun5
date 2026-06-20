package com.example.modun5.expense.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatDTO {

    private Long       categoryId;
    private String     categoryName;
    private String     categoryIcon;
    private String     categoryColor;
    private BigDecimal totalAmount;
    private Long       transactionCount;
    private Double     percentage; // % trên tổng chi tiêu, tính ở Service layer
}
