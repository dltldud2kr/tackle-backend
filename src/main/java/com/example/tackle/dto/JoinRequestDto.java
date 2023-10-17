package com.example.tackle.dto;


import lombok.Data;

@Data
public class JoinRequestDto {
    private String userId;
    private String password; //비밀번호
    private String userName;
    private String nickname;
    private int platform; //가입 플랫폼 0:flatform 1:kakao 2:naver
}
