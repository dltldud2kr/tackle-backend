package com.example.tackle.voteItems.service;

import com.example.tackle.votingBoard.dto.VotingBoardDto;
import org.springframework.stereotype.Service;

@Service
public interface VoteItemsService {

    boolean create(Long postId, VotingBoardDto dto);

}
