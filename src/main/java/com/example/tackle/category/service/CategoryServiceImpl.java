package com.example.tackle.category.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.category.dto.CategoryDto;
import com.example.tackle.category.entity.Category;
import com.example.tackle.category.repository.CategoryRepository;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    /**
     * 관리자 판별 메서드
     **/
    private boolean checkIfAdmin(String idx) {
        return memberRepository.findByIdx(idx)
                .map(member -> member.getRole() == 1)
                .orElse(false);
    }

    public boolean create(String idx, CategoryDto dto) {
        if (!checkIfAdmin(idx)) {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }

        Category category = Category.builder()
                .categoryName(dto.getCategoryName())
                .build();
        categoryRepository.save(category);
        return true;
    }


    public boolean delete(Long categoryId, String idx) {
        if (!checkIfAdmin(idx)) {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND);
        }
        categoryRepository.deleteById(categoryId);
        return true;
    }

    @Override
    public List<CategoryDto> getCategory(String idx) {
        if (!checkIfAdmin(idx)) {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }
        List<Category> categoryList = categoryRepository.findAll();
        return convertToDtoList(categoryList);
    }

    /** Entity -> Dto 변환 **/
    private List<CategoryDto> convertToDtoList(List<Category> categories) {
        return categories.stream()
                .map(category -> CategoryDto.builder()
                        .categoryId(category.getCategoryId())
                        .categoryName(category.getCategoryName())
                        .build())
                .collect(Collectors.toList());
    }

}
