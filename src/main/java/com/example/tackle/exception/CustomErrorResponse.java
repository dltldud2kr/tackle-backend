package com.example.tackle.exception;

import com.example.tackle._enum.CustomExceptionCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomErrorResponse {
    private CustomExceptionCode status;
    private String statusMessage;
}
