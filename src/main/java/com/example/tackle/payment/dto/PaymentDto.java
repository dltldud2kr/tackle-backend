package com.example.tackle.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentDto {
    private Long payment_Id;

    private String idx; // 카카오 고유번호 12자리
    private Long payment_amount;
    private String impUid; // 결제 고유 번호 , 중복 방지
    private LocalDateTime payment_date;
}