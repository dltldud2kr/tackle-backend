package com.example.tackle.member.service;

import com.example.tackle.dto.TokenDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MemberService {

    /**
     * 회원 등록
     */
    TokenDto join(String email, String memberIdx);


    TokenDto login(String email, String password);

    TokenDto createToken(String memberIdx);

    /**
     *
     * 카카오 로그인 토큰 값
     */
    String getReturnAccessToken(String code);

    /**
     * 카카오로그인 파싱 결과
     * @param access_token
     * @return
     */
    public Map<String,Object> getUserInfo(String access_token);

}
