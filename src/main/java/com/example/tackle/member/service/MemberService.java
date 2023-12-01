package com.example.tackle.member.service;

import com.example.tackle.dto.TokenDto;
import com.example.tackle.member.dto.MemberDto;
import com.example.tackle.member.entity.Member;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public interface MemberService {

    /**
     * 회원 등록
     */
    TokenDto join(String email, String memberIdx, String nickname);


    TokenDto login(String email, String password);

    TokenDto createToken(String memberIdx);

    /**
     *
     * 카카오 로그인 토큰 값
     */
    String getReturnAccessToken(String code, HttpServletRequest request);

    /**
     * 카카오로그인 파싱 결과
     * @param access_token
     * @return
     */
    public Map<String,Object> getUserInfo(String access_token);


    /**
     * @param access_token
     * @return
     */
    List<Member> getMemberList(String access_token);

    boolean update(String idx, Member dto);


    /**
     *
     * @param access_token
     * @return
     */
    MemberDto getMemberInfo(String access_token);
}
