package com.example.tackle.voteItems;

import com.example.tackle.votingBoard.VotingBoardDto;
import org.springframework.stereotype.Service;

@Service
public interface VoteItemsService {

    boolean create(Long postId, VotingBoardDto dto);

}
