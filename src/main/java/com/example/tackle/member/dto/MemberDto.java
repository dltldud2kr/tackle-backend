package com.example.tackle.member.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class MemberDto {
    private String nickname;
    private LocalDateTime regAt;
    private Long point;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public LocalDateTime getRegAt() {
        return regAt;
    }

    public void setRegAt(LocalDateTime regAt) {
        this.regAt = regAt;
    }

    public Long getPoint() {
        return point;
    }

    public void setPoint(Long point) {
        this.point = point;
    }


}






