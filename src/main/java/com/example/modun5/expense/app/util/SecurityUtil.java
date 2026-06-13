
package com.example.modun5.expense.app.util;

import com.example.modun5.expense.app.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private static UserRepository userRepository;

    public SecurityUtil(UserRepository userRepository) {
        SecurityUtil.userRepository = userRepository;
    }

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("Chưa đăng nhập");
        }
        return auth.getName();
    }

    public static Long getCurrentUserId() {
        return userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy user"))
                .getId();
    }
}
