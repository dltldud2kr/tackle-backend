package com.example.tackle.voteItems.service;


import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.exception.CustomException;
import com.example.tackle.voteItems.entity.VoteItems;
import com.example.tackle.voteItems.repository.VoteItemsRepository;
import com.example.tackle.votingBoard.dto.VotingBoardDto;
import com.example.tackle.votingBoard.repository.VotingBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteItemsServiceImpl implements VoteItemsService {
    private final VoteItemsRepository voteItemsRepository;

    private final VotingBoardRepository votingBoardRepository;

    @Transactional
    public boolean create(Long postId, List<String> items) {

        votingBoardRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));


        if(items.size() < 2){        // 2개 이상의 투표항목을 선택해야함.
            throw new CustomException(CustomExceptionCode.NOT_ENOUGH_ITEMS);
        }

        for(String ListItems : items){
            VoteItems voteItems = VoteItems.builder()
                    .voteCount(0L)
                    .content(ListItems)
                    .postId(postId)
                    .build();
            voteItemsRepository.save(voteItems);
        }

        return true;
    }
}
