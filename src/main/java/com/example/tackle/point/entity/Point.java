package com.example.tackle.point.entity;

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
public class Point {
    @Id
    @GeneratedValue
    private Long pointId;

    private String idx;
    private Integer pointAccumulationReason;    // 0. 게시글작성 1. 승리 2. 패배 3.베팅 4. 포인트 충전
    private Long pointChangeAmount;
    private LocalDateTime pointUsingDate;

}
