package com.example.tackle.replies.controller;

import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.exception.CustomException;
import com.example.tackle.replies.dto.RepliesDto;
import com.example.tackle.replies.service.RepliesService;
import com.example.tackle.voteResult.dto.VoteResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/replies")
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

    @GetMapping("/myinfo")
    public List<RepliesDto> getMyReplies(@RequestParam Long idx ){
        try{
            List<RepliesDto> myReplies = repliesService.getMyRepliesInfo(idx);
            return myReplies;
        } catch (Exception e){
            return (List<RepliesDto>) ResultDTO.of(false, ApiResponseCode.INTERNAL_SERVER_ERROR.getCode(), "댓글 ㅇ 실패.", null);
        }
    }

    @DeleteMapping("/delete")
    public ResultDTO deleteReply(@RequestParam Long repliesId) {
        try {
            return ResultDTO.of(repliesService.delete(repliesId), ApiResponseCode.SUCCESS.getCode(), "댓글이 삭제 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


}
