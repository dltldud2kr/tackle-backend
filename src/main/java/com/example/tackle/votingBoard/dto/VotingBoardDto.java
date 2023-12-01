package com.example.tackle.votingBoard.dto;

import com.example.tackle._enum.VotingStatus;
import com.example.tackle.voteItems.entity.VoteItems;
import com.example.tackle.votingBoard.entity.VotingBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private VotingStatus status;
    private String votingImgUrl;
    private Long votingResult;
    private String nickname;
    private Long votingAmount;
    private Long bettingAmount;
    private byte votingDeadLine;
    private List<String> voteItemsContent;
    private LocalDateTime createdAt;
    private LocalDateTime endDate;


    //    public  List<VotingBoardDto> of (List<VotingBoard> votingBoards) {
//
//        if (votingBoards == null) {
//            return null;
//        }
//
//        List<VotingBoardDto> votingBoardList = new ArrayList<>();
//        for (VotingBoard x : votingBoards) {
//            votingBoardList.add(of(x));
//        }
//        return votingBoardList;
//
//    }
//
//    public  VotingBoardDto of(VotingBoard votingBoard){
//
//        return VotingBoardDto.builder()
//
//                .categoryId(votingBoard.getCategoryId())
//                .votingImgUrl(votingBoard.getVotingImgUrl())
//                .status(votingBoard.getStatus())
//                .votingResult(votingBoard.getVotingResult())
//                .content(votingBoard.getContent())
//                .title(votingBoard.getTitle())
//                .idx(votingBoard.getIdx())
//                .nickname(votingBoard.getNickname())
//                .endDate(votingBoard.getEndDate())
//                .postId(votingBoard.getPostId())
//                .bettingAmount(votingBoard.getTotalBetAmount())
//                .createdAt(votingBoard.getCreatedAt())
//                .build();
//    }
    public List<VotingBoardDto> of(List<VotingBoard> votingBoards, List<VoteItems> voteItemsList) {
        if (votingBoards == null) {
            return null;
        }

        List<VotingBoardDto> votingBoardList = new ArrayList<>();
        for (VotingBoard x : votingBoards) {
            votingBoardList.add(of(x, voteItemsList));
        }
        return votingBoardList;
    }
    public VotingBoardDto of(VotingBoard votingBoard, List<VoteItems> voteItemsList) {
        VotingBoardDto dto = VotingBoardDto.builder()
                .categoryId(votingBoard.getCategoryId())
                .votingImgUrl(votingBoard.getVotingImgUrl())
                .status(votingBoard.getStatus())
                .votingResult(votingBoard.getVotingResult())
                .content(votingBoard.getContent())
                .title(votingBoard.getTitle())
                .idx(votingBoard.getIdx())
                .votingAmount(votingBoard.getVotingAmount())
                .nickname(votingBoard.getNickname())
                .endDate(votingBoard.getEndDate())
                .postId(votingBoard.getPostId())
                .bettingAmount(votingBoard.getTotalBetAmount())
                .createdAt(votingBoard.getCreatedAt())
                .build();

        // 투표 항목 String 값
        List<String> voteItemsContent = new ArrayList<>();
        for (VoteItems voteItem : voteItemsList) {
            voteItemsContent.add(voteItem.getContent());
        }

        dto.setVoteItemsContent(voteItemsContent);

        return dto;
    }

}
