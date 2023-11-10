package com.example.tackle.votingBoard.service;

import com.example.tackle.voteResult.dto.VoteResultDto;
import com.example.tackle.votingBoard.dto.VotingBoardDto;
import com.example.tackle.votingBoard.dto.VotingBoardResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VotingBoardService {

    /**
     * 게시글 작성
     * @param dto
     * @return
     */
    Long create (VotingBoardDto dto);

    /**
     * 게시글 리스트 조회
     * @return
     */

    List<VotingBoardDto> getBoardList();

    /**
     * 게시글 조회
     * @param boardId
     * @return
     */
    VotingBoardResponseDto getBoardInfo(Long boardId, String id);

    boolean voting(VoteResultDto dto);


    boolean delete(String email, Long postId);
}
