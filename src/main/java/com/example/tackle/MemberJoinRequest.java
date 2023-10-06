package com.example.tackle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MemberJoinRequest {
    private String userId;
    private String userName;
    private String password;
    private String nickname;
    private LocalDateTime regDt;


}
