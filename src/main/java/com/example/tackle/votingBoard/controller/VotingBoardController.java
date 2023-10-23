package com.example.tackle.votingBoard.controller;

import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.exception.CustomException;
import com.example.tackle.votingBoard.VotingBoardDto;
import com.example.tackle.votingBoard.service.VotingBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/board")
@Tag(name = "게시판 API", description = "")
public class VotingBoardController {

    private final VotingBoardService votingBoardService;



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
            return ResultDTO.of(votingBoardService.create(dto), ApiResponseCode.SUCCESS.getCode(), "게시글 작성완료.",null);

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
    public ResultDTO<VotingBoardDto> getBoard(@RequestParam("boardId") long boardId) {
        try {
            VotingBoardDto boardInfo = votingBoardService.getBoardInfo(boardId);
            return ResultDTO.of(boardInfo != null, ApiResponseCode.SUCCESS.getCode(), boardInfo != null ? "성공" : "해당 StoreIdx 정보를 찾을 수 없습니다.",boardInfo);

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


}
