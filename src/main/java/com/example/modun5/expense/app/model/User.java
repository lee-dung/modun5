package com.example.modun5.expense.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String avatarUrl;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
