package com.example.modun5.expense.app.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    public enum Type { EXPENSE, INCOME, BOTH}
    private Long id;
    private Long userId;
    private String name;
    private String icon;
    private String color;
    private Type type;
    private LocalDateTime createdAt;
}
