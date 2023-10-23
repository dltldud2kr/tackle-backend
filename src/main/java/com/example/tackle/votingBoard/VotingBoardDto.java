package com.example.tackle.votingBoard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VotingBoardDto {


    private Long postId;

    private String idx;       //회원 idx
    private Long categoryId;
    private String title;
    private String content;
    private String status;
    private String votingImgUrl;
    private String votingResult;
    private Long bettingAmount;
    private LocalDateTime createdAt;
    private LocalDateTime endDate;
}
