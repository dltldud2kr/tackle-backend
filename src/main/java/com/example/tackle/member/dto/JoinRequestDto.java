package com.example.tackle.member.dto;


import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor(staticName = "of")
public class JoinRequestDto<D> {
    private final boolean success;
    private final String type;
    private final String resultCode;
    private final String message;
    private final D data;
}
