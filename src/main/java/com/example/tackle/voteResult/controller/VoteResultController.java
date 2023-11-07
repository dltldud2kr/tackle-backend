package com.example.tackle.voteResult.controller;


import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.exception.CustomException;
import com.example.tackle.voteResult.entity.VoteResult;
import com.example.tackle.voteResult.service.VoteResultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/voteResult")
@Tag(name = "투표결과 API", description = "")
public class VoteResultController {

    private final VoteResultService voteResultService;

    @Operation(summary = "내 투표 리스트 조회", description = "내 투표 리스트를" +
            " 요청합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 리스트 조회 성공 "+
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_FOUND: 투표항목이 없습니다.")

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "OK"),
    })

    @GetMapping("/info")
    public ResultDTO voteResultInfo(Principal principal) {
        String email = "";
        if (principal == null) {
            email = "";
        } else {
            email = principal.getName(); // 사용자가 로그인한 경우 이메일 가져오기
        }

        List<VoteResult> list = voteResultService.list(email);
        try{
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "조회 성공", list);
        }catch (CustomException e){
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }

}
