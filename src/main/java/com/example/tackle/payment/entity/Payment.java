package com.example.tackle.payment.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Payment {
        @Id
        @GeneratedValue
        private Long payment_Id;

        private String idx; // 카카오 고유번호 12자리
        private Long payment_amount;
        private String impUid; // 결제 고유 번호 , 중복 방지
        private LocalDateTime payment_date;
}

