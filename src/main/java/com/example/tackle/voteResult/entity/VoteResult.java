package com.example.tackle.voteResult.entity;

import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle._enum.VotingStatus;
import lombok.*;

import javax.persistence.*;
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

    private Long postId;
    private String idx;       // 회원idx
    private Long itemId;
    @Enumerated(EnumType.STRING)
    private VotingResultStatus status;  // 진행중, 승리, 패배
    private Long bettingPoint;
    private Long getPoint;
    private LocalDateTime createdAt;

}
