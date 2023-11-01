package com.example.tackle.voteResult.dto;

import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle._enum.VotingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VoteResultDto {

    private Long postId;
    private String idx;       // 회원idx
    private Long itemId;
    private VotingResultStatus status;
    private Long bettingPoint;
    private Long getPoint;
    private LocalDateTime createdAt;
}
