package com.example.tackle.votingBoard.service;

import com.example.tackle.voteResult.dto.VoteResultDto;
import com.example.tackle.votingBoard.dto.VotingBoardDto;
import com.example.tackle.votingBoard.dto.VotingBoardResponseDto;
import com.example.tackle.votingBoard.entity.VotingBoard;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VotingBoardService {

    /**
     * 게시글 작성
     *
     * @param dto
     * @return
     */
    Long create(VotingBoardDto dto);



    /**
     * 게시글 리스트 조회
     *
     * @return
     */
    List<VotingBoardDto> getBoardList();

    List<VotingBoardDto> getMyBoardList(String idx);

    /**
     * 카테고리별 게시글 리스트
     * @param categoryId
     * @return
     */

    List<VotingBoardDto> getBoardListByCategory(Long categoryId);

    /**
     * 게시글 조회
     *
     * @param boardId
     * @return
     */
    VotingBoardResponseDto getBoardInfo(Long boardId, String id);

    /**
     * 투표
     * @param dto
     * @return
     */

    boolean voting(VoteResultDto dto);

    /**
     * 게시글 삭제
     * @param email
     * @param postId
     * @return
     */

    boolean delete(String email, Long postId);

    /**
     * 게시글 검색
     * @param keyword
     * @return
     */
    List<VotingBoard> search(String keyword);

}
