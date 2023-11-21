package com.example.tackle.myPage;

import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.replies.dto.RepliesDto;
import com.example.tackle.replies.service.RepliesService;
import com.example.tackle.voteResult.dto.VoteResultDto;
import com.example.tackle.voteResult.entity.VoteResult;
import com.example.tackle.voteResult.service.VoteResultService;
import com.example.tackle.votingBoard.dto.VotingBoardDto;
import com.example.tackle.votingBoard.service.VotingBoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
@Tag(name = "마이페이지 API", description = "")
public class MyPageController {

    private final RepliesService repliesService;
    private final VotingBoardService votingBoardService;
    private final MemberRepository memberRepository;
    private final VoteResultService voteResultService;


    @GetMapping("/myBoard")
    public ResultDTO myBoardList(Principal principal){
        String email = getEmailFromPrincipal(principal);

        List<VotingBoardDto> votingBoardDtoList = votingBoardService.getMyBoardList(email);

        try {
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "조회 성공",votingBoardDtoList);

        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }

    @GetMapping("/myPoint")
    public ResultDTO myPointList(Principal principal){
        return null;
    }


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
    @GetMapping("/myVote")
    public ResultDTO myVoteList(Principal principal){
        String email = getEmailFromPrincipal(principal);

        List<VoteResult> voteResultList = voteResultService.list(email);
        try {
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "조회 성공",voteResultList);

        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }



    @GetMapping("/myReply")
    public ResultDTO MyReplyList(Principal principal){
        String email = getEmailFromPrincipal(principal);
        List<RepliesDto> myReplies = repliesService.getMyRepliesInfo(email);

        try {
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "조회 성공",myReplies);


        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }

    private String getEmailFromPrincipal(Principal principal) {
        String email = "";
        if (principal != null) {
            email = principal.getName(); // 사용자가 로그인한 경우 이메일 가져오기
        }
        return email;
    }



}
