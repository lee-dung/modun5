package com.example.modun5.expense.app.service;

import com.example.modun5.expense.app.model.User;
import java.util.Optional;
import com.example.modun5.expense.app.dto.RegisterDTO;

public interface UserService {
    User register(RegisterDTO dto);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
