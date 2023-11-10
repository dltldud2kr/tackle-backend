package com.example.tackle.votingBoard.controller;

import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.exception.CustomException;
import com.example.tackle.voteItems.service.VoteItemsService;
import com.example.tackle.voteResult.dto.VoteResultDto;
import com.example.tackle.votingBoard.dto.GetBoardDto;
import com.example.tackle.votingBoard.dto.VotingBoardDto;
import com.example.tackle.votingBoard.dto.VotingBoardResponseDto;
import com.example.tackle.votingBoard.service.VotingBoardService;
import com.fasterxml.jackson.databind.JsonNode;
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
@RequestMapping("/api/v1/board")
@Tag(name = "게시판 API", description = "")
public class VotingBoardController {

    private final VotingBoardService votingBoardService;
    private final VoteItemsService voteItemsService;



    @Operation(summary = "게시글 작성", description = "" +
            "게시글을 작성합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 "+
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 작성 성공"),
    })

    @PostMapping("/create")
    public ResultDTO create(@RequestBody VotingBoardDto dto){

        try {
            // 게시글작성, 투표항목 로직 한 번에 처리
            Long postId = votingBoardService.create(dto);
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "게시글 작성완료.",postId);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "게시글 정보 조회", description = "" +
            "게시글을 조회합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 "+
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
    })

    @GetMapping("/info")
    public ResultDTO<VotingBoardResponseDto> getBoard(@RequestParam Long postId, Principal principal) {

        String email = "";
        if (principal == null) {
            email = "";
        } else {
            email = principal.getName(); // 사용자가 로그인한 경우 이메일 가져오기
        }

        System.out.println("id = " + email);
        try {
            VotingBoardResponseDto boardInfo = votingBoardService.getBoardInfo(postId, email);
            return ResultDTO.of(boardInfo != null, ApiResponseCode.SUCCESS.getCode(), boardInfo != null ? "성공" : "해당 PostId 정보를 찾을 수 없습니다.",boardInfo);

        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @Operation(summary = "게시글 리스트 조회", description = "" +
            "게시글 리스트를 조회합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 "+
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 리스트 조회 성공"),
    })

    @GetMapping("/list")
    public List<VotingBoardDto> boardList() {

        List<VotingBoardDto> list = votingBoardService.getBoardList();

        return list;
    }

    @Operation(summary = "게시글 투표", description = "" +
            "게시글 투표를 합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 "+
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- NOT_ENOUGH_ITEMS\": \"선택지 항목이 2개 이상이어야 합니다." +
            "\n- INVALID_DEADLINE\": \"기한은 1~7일 사이로 설정해야합니다." +
            "\n- EXPIRED_VOTE\": \"투표기간이 만료되었습니다."+
            "\n- ALREADY_VOTED\": \"이미 투표를 완료했습니다."
            )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 투표 성공"),
    })
    //진행중
    @PostMapping("/voting")
    public ResultDTO  voting( @RequestBody VoteResultDto dto){

        try {
            return ResultDTO.of(votingBoardService.voting(dto), ApiResponseCode.SUCCESS.getCode(), "투표 성공", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
        //test
    }


    @DeleteMapping("/delete")
    public ResultDTO delete(@RequestParam Long postId, Principal principal){
        String email = "";
        if (principal == null) {
            // 사용자가 로그인하지 않은 경우에 대한 처리
            email = "";

        } else {
            email = principal.getName(); // 사용자가 로그인한 경우 이메일 가져오기
        }

        try {
            return ResultDTO.of(votingBoardService.delete(email,postId), ApiResponseCode.SUCCESS.getCode(), "삭제 성공",null);

        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }


}
