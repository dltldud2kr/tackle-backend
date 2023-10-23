package com.example.tackle.voteResult;

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
public class VoteResult {

    @Id
    @GeneratedValue
    private Long resultId;

    private Long idx;       // 회원idx
    private Long itemId;
    private String status;  //enum으로 바꿀 수도 있음.
    private Long bettingPoint;
    private Long getPoint;
    private LocalDateTime createdAt;

}
