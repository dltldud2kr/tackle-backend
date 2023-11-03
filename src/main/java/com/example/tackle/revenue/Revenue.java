package com.example.tackle.revenue;

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
public class Revenue {

    @Id
    @GeneratedValue
    private Long revenueId;
    private Long earningPoint;
    private Integer earningReason; // 0. 투표 수수료
    private LocalDateTime created_at;
}
