package com.example.tackle.point;

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
    private String pointAccumulationReason;
    private Long pointChangeAmount;
    private LocalDateTime pointUsingDate;

}
