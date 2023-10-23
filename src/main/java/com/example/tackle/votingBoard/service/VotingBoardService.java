package com.example.tackle.votingBoard.service;

import com.example.tackle.votingBoard.VotingBoardDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VotingBoardService {

    boolean create (VotingBoardDto dto);

    List<VotingBoardDto> getBoardList();

    VotingBoardDto getBoardInfo(long boardId);
}
