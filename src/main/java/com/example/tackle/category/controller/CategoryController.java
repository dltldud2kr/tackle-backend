package com.example.tackle.category.controller;

import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle.category.dto.CategoryDto;
import com.example.tackle.category.service.CategoryService;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.exception.CustomException;
import com.example.tackle.replies.dto.RepliesDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/category")
@Tag(name = "카테고리 API", description = "")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/create")
    public ResultDTO create(@RequestParam String idx, @RequestBody CategoryDto dto) {
        try {
            return ResultDTO.of(categoryService.create(idx, dto), ApiResponseCode.SUCCESS.getCode(), "카테고리 생성 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @GetMapping("/info")
    public List<CategoryDto> getCategory(@RequestParam String idx ){
        try{
            List<CategoryDto> categoryInfo = categoryService.getCategory(idx);
            return categoryInfo;
        } catch (Exception e){
            return (List<CategoryDto>) ResultDTO.of(false, ApiResponseCode.INTERNAL_SERVER_ERROR.getCode(), "카테고리 조회 실패", null);
        }
    }

    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestParam Long categoryId, String idx) {

        try {
            return ResultDTO.of(categoryService.delete(categoryId, idx), ApiResponseCode.SUCCESS.getCode(), "카테고리 삭제 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }



}
