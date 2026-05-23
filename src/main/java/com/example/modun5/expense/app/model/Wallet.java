package com.example.modun5.expense.app.model;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {
    private Long id;
    private Long userId;
    private String name;
    private BigDecimal balance;
    private String currency;
    private String icon;
    private String description;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
