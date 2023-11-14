package com.example.tackle.votingBoard.entity;

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
public class VotingBoard {

    @Id
    @GeneratedValue
    private Long postId;

    private String idx;           //회원 idx
    private Long categoryId;
    private String title;
    private String content;
    @Enumerated(EnumType.STRING)
    private VotingStatus status;      // 진행중, 종료 enum 타입 고려해볼것
    private String votingImgUrl;
    private Long votingResult;
    private Long totalBetAmount;
    private byte votingDeadLine;

    private LocalDateTime createdAt;
    private LocalDateTime endDate;

}
