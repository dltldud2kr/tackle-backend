package com.example.tackle.member.controller;

import com.example.tackle.member.Member;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.member.service.MemberService;
import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle.dto.JoinRequestDto;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.dto.TokenDto;
import com.example.tackle.dto.loginRequestDto;
import com.example.tackle.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
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


    @Operation(summary = "로그인 요청", description = "" +
            "회원 로그인을 요청하고 토큰을 발급합니다." +
            "\n### HTTP STATUS 에 따른 요청 결과" +
            "\n- 200: 서버요청 정상 성공" +
            "\n- 403: 회원정보 인증 실패" +
            "\n- 500: 서버에서 요청 처리중 문제가 발생했습니다." +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- SUCCESS: 로그인 성공 및 정상 토큰 발급" +
            "\n- NOT_FOUND_EMAIL: 요청한 이메일 가입자가 존재하지 않음")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
    })
    @PostMapping("/auth/login")
    public ResultDTO<TokenDto> login(@RequestBody loginRequestDto loginRequest) {

        try {
            String email = loginRequest.getEmail();
            String memberIdx = loginRequest.getMemberIdx();

            TokenDto tokenDto = memberService.login(email,  memberIdx);

            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "로그인 성공", tokenDto);
        } catch (CustomException e) {
            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
        }

    }
    @Operation(summary = "카카오 인가코드 발급 및 로그인, 회원가입", description = "로그인 및 회원가입" +
            "임시 회원가입을 요청합니다." +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 201: 회원가입 성공 "+
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n- DUPLICATED: 동일한 이메일이 존재합니다.")

    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "OK"),
    })


//    @PostMapping("/auth/join")
//    public ResultDTO join(
//            @RequestBody JoinRequestDto joinRequestDto) {
//        try {
//            return ResultDTO.of(memberService.join(joinRequestDto), ApiResponseCode.CREATED.getCode(), "회원가입이 완료되었습니다.", null);
//        } catch (CustomException e) {
//            return ResultDTO.of(false, e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
//        }
//    }




    @GetMapping("/auth/kakao/callback")
    public @ResponseBody JoinRequestDto<Object> kakaoCallback(String code)  {
        System.out.println("code: " + code);

        // 접속토큰 get
        String kakaoToken = memberService.getReturnAccessToken(code);

        // 접속자 정보 get
        // id, connected_at , prop
        Map<String, Object> result = memberService.getUserInfo(kakaoToken);
        log.info("result:: " + result);
        String idx = (String) result.get("id");
        String userName = (String) result.get("nickname");
        String email = (String) result.get("email");


        Optional<Member> member =  memberRepository.findById(idx);
        if (member.isPresent()){
            try {
                TokenDto tokenDto = memberService.login(email, idx);

                return JoinRequestDto.of(true,"기존회원",
                        ApiResponseCode.SUCCESS.getCode(), "로그인 성공", tokenDto);
            } catch (CustomException e) {
                return JoinRequestDto.of(false, "에러",
                        e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);
            }
        } else {
            return JoinRequestDto.of(memberService.join(email,idx),"신규회원",
                    ApiResponseCode.CREATED.getCode(), "회원가입이 완료되었습니다.", null);
        }

    }






}
