package com.example.tackle.category.service;

import com.example.tackle.category.dto.CategoryDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {
    boolean create(String idx, CategoryDto dto);

    boolean delete(Long categoryId, String idx);

    List<CategoryDto> getCategory(String idx);
}
