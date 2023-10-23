package com.example.tackle.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class loginRequestDto {

    private String userId;
    private String password;
}



