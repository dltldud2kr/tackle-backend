package com.example.tackle.votingBoard.dto;

import com.example.tackle._enum.VotingResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
    private VotingResultStatus votingResult;
    private Long bettingAmount;
    private byte votingDeadLine;
    private List<String> voteItemsContent;
    private LocalDateTime createdAt;
    private LocalDateTime endDate;
}
