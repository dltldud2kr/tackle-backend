package com.example.tackle.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class loginRequestDto {

    private String userId;
    private String password;
}
