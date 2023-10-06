package com.example.tackle;

import com.example.tackle.dto.loginRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface MemberService {

    /**
     * 회원 등록
     */
    ResponseEntity join(MemberJoinRequest dto);

    ResponseEntity login(loginRequestDto dto);

}
