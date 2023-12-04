package com.example.tackle.member.controller;

import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.dto.TokenDto;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.dto.MemberDto;
import com.example.tackle.member.entity.Member;
import com.example.tackle.member.dto.JoinRequestDto;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.member.service.MemberService;
import com.example.tackle.replies.dto.RepliesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "회원 API", description = "")
public class MemberController {
    private final MemberRepository memberRepository;
    private final MemberService memberService;


    @Operation(summary = "Access Token 발급 요청", description = "" +
            "RefreshToken으로 Access Token 발급을 요청합니다." +
            "\n### 토큰 별 유효기간" +
            "\n- AccessToken: 2시간" +
            "\n- RefreshToken: 3일" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 401: 만료된 토큰이거나, 잘못된 토큰" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 발급 성공"),
    })
    @PostMapping("/auth/token")
    public ResultDTO<TokenDto> getAccessToken(@RequestBody String memberIdx) {
        try {
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "토큰이 갱신 되었습니다.", memberService.createToken(memberIdx));
        } catch (CustomException e) {
            memberService.createToken(memberIdx);
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "카카오 인가코드 발급 및 로그인, 회원가입", description = "로그인 및 회원가입" +
            "임시 회원가입을 요청합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 201: 회원가입 성공 " +
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- DUPLICATED: 동일한 이메일이 존재합니다.")

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "OK"),
    })


    @GetMapping("/auth/kakao/callback")
    public @ResponseBody JoinRequestDto<Object> kakaoCallback(String code, HttpServletRequest request) {
        System.out.println("code: " + code);

        // 접속토큰 get
        String kakaoToken = memberService.getReturnAccessToken(code, request);

        // 접속자 정보 get
        // id, connected_at , prop
        Map<String, Object> result = memberService.getUserInfo(kakaoToken);
        log.info("result:: " + result);
        String idx = (String) result.get("id");
        String nickname = (String) result.get("nickname");
        String email = (String) result.get("email");
        String profileImage = (String) result.get("profileImage");


        Optional<Member> member = memberRepository.findById(idx);
        if (member.isPresent()) {
            try {
                TokenDto tokenDto = memberService.login(email, idx);

                JoinRequestDto<Object> response = JoinRequestDto.of(true, "기존회원",
                        ApiResponseCode.SUCCESS.getCode(), "로그인 성공했음.", tokenDto);
                response.setUserInfo(result);  // 사용자 정보를 설정합니다.
                return response;
            } catch (CustomException e) {
                JoinRequestDto<Object> response = JoinRequestDto.of(false, "에러",
                        e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
                response.setUserInfo(result);  // 사용자 정보를 설정합니다.
                return response;
            }
        } else {
            TokenDto tokenDto = memberService.join(email, idx, nickname);
            JoinRequestDto<Object> response = JoinRequestDto.of(true, "신규회원",
                    ApiResponseCode.CREATED.getCode(), "회원가입이 완료되었습니다.", tokenDto);
            response.setUserInfo(result);  // 사용자 정보를 설정합니다.
            return response;
        }
    }

    @Operation(summary = "관리자 회원 리스트 조회", description = "회원의 데이터 반환" +
            "")

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    // 관리자 페이지 회원 리스트 조회
    @GetMapping("/member/list")
    public ResponseEntity<List<Member>> getMemberList(Principal principal) {
        String email = "";
        if (principal == null) {
            // 사용자가 로그인하지 않은 경우에 대한 처리
            email = "";

        } else {
            email = principal.getName(); // 사용자가 로그인한 경우 이메일 가져오기
        }
        try {
            List<Member> members = memberService.getMemberList(email);
            return new ResponseEntity<>(members, HttpStatus.OK);

        } catch (CustomException e) {
            return null;
        }
    }




    @Operation(summary = "닉네임 변경", description = "")
    @PostMapping("/member/update")
    public ResultDTO update(@RequestParam String idx, @RequestBody Member dto){
        try {
            return ResultDTO.of(memberService.update(idx, dto), ApiResponseCode.SUCCESS.getCode(), "닉네임 변경 완료.", null);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }
    }


    @Operation(summary = "회원정보 조회", description = "회원의 데이터 반환" +
            "")

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
    })
    // 회원 포인트, 가입일, 닉네임 조회 ( 마이페이지 Function )
    // 엑세스토큰 header에 넣어서 GET요청
    @GetMapping("/member/info")
    public ResponseEntity<MemberDto> getMemberInfo(Principal principal) {
        // 토큰에서 이메일 추출
        String access_token = "";
        if (principal == null) {
            // 사용자가 로그인하지 않은 경우에 대한 처리
            access_token = "";

        } else {
            access_token = principal.getName(); // 사용자가 로그인한 경우 이메일 가져오기
        }

        // 이메일로 멤버 정보 가져오기
        MemberDto memberDto = memberService.getMemberInfo(access_token);

        // 멤버 정보가 없는 경우 예외 처리
        if (memberDto == null) {
            throw new CustomException(CustomExceptionCode.NOT_FOUND);
        }

        // 멤버 정보 반환
        return ResponseEntity.ok(memberDto);
    }


}
