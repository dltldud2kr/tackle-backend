package com.example.tackle.member.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class loginRequestDto {

    private String email;
    private String memberIdx;
}



