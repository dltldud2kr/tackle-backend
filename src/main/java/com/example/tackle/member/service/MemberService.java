package com.example.tackle.member.service;

import com.example.tackle.dto.JoinRequestDto;
import com.example.tackle.dto.TokenDto;
import com.example.tackle.dto.loginRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    /**
     * 회원 등록
     */
    boolean join(JoinRequestDto request);

    TokenDto login(String userId, String password);

    TokenDto createToken(Long memberIdx);

}
