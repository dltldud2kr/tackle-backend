package com.example.tackle.votingBoard.dto;

import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle._enum.VotingStatus;
import com.example.tackle.voteItems.entity.VoteItems;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class VotingBoardResponseDto {


    private Long postId;

    private String idx;       //회원 idx
    private Long categoryId;
    private String title;
    private String content;
    private VotingStatus status;
    private String votingImgUrl;
    private Long votingResult;
    private Long bettingAmount;
    private byte votingDeadLine;
    private List<String> voteItemsContent;
//    private List<Long> voteItemsId;
    private LocalDateTime createdAt;
    private LocalDateTime endDate;
    private boolean isVoting;
    private Map<Long, Long> voteItemIdMap;


}
