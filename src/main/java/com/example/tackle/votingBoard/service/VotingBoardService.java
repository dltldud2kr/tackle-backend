package com.example.tackle.votingBoard.service;

import com.example.tackle.voteResult.VoteResult;
import com.example.tackle.voteResult.VoteResultDto;
import com.example.tackle.votingBoard.dto.VotingBoardDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VotingBoardService {

    /**
     * 게시글 작성
     * @param dto
     * @return
     */
    boolean create (VotingBoardDto dto);

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
    VotingBoardDto getBoardInfo(Long boardId);

    boolean voting(VoteResultDto dto);


}
