package com.example.modun5.expense.app.service;

import com.example.modun5.expense.app.model.User;
import com.example.modun5.expense.app.dto.RegisterDTO;
import com.example.modun5.expense.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterDTO dto) {
        if (userRepo.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException(" Tên đăng nhập đã được sử dụng");
        }
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email đã được đăng ký");
        }
        if (!dto.isPasswordMatching()) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }
        User user = User.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(passwordEncoder.encode(dto.getPassword()))  // QUAN TRỌNG: không lưu plain text
                .enabled(true)
                .build();

        return userRepo.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }
}
