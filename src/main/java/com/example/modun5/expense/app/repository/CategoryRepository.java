package com.example.modun5.expense.app.repository;

import com.example.modun5.expense.app.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findAllByUserIdOrSystem(Long userId);

    List<Category> findByUserIdAndType(Long userId, String type);

    Optional<Category> findById(Long id);

    Category save(Category category);

    void update(Category category);

    void deleteByIdAndUserId(Long id, Long userId);
}
