package com.example.tackle.replies.controller;

import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.exception.CustomException;
import com.example.tackle.replies.dto.RepliesDto;
import com.example.tackle.replies.service.RepliesService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replies")
@Tag(name = "댓글 API", description = "")
public class RepliesController {

    private final RepliesService repliesService;

    @PostMapping("/create")
    public ResultDTO create(@RequestBody RepliesDto dto) {
        try {
            return ResultDTO.of(repliesService.create(dto), ApiResponseCode.SUCCESS.getCode(), "댓글 작성 완료.", null);
        } catch (CustomException e) {

            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @GetMapping("/info")
    public List<RepliesDto> getReplies(@RequestParam Long postId) {
        try {
            List<RepliesDto> replies = repliesService.getRepliesInfo(postId);
            return replies;
        } catch (Exception e) {
            return (List<RepliesDto>) ResultDTO.of(false, ApiResponseCode.INTERNAL_SERVER_ERROR.getCode(), "댓글 조회 실패.", null);
        }
    }

//    @GetMapping("/myinfo")
//    public List<RepliesDto> getMyReplies(@RequestParam String idx ){
//        try{
//            List<RepliesDto> myReplies = repliesService.getMyRepliesInfo(idx);
//            return myReplies;
//        } catch (Exception e){
//            return (List<RepliesDto>) ResultDTO.of(false, ApiResponseCode.INTERNAL_SERVER_ERROR.getCode(), "댓글 실패.", null);
//        }
//    }

    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestParam Long repliesId, String idx) {
        try {
            return ResultDTO.of(repliesService.delete(repliesId, idx), ApiResponseCode.SUCCESS.getCode(), "댓글 삭제 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @PostMapping("/update")
    public ResultDTO update(@RequestParam Long repliesId, String idx, @RequestBody RepliesDto dto){
        try {
            return ResultDTO.of(repliesService.update(repliesId, idx, dto), ApiResponseCode.SUCCESS.getCode(), "댓글 수정 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

}
