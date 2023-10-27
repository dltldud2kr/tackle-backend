package com.example.tackle.voteResult;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.exception.CustomException;
import com.example.tackle.voteItems.entity.VoteItems;
import com.example.tackle.voteItems.repository.VoteItemsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteResultServiceImpl implements VoteResultService {

private final VoteItemsRepository voteItemsRepository;

    @Override
    public boolean register(Long itemId) {
        VoteItems voteItems = voteItemsRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

//        VoteResult voteResult = VoteResult.builder()
//                .createdAt(LocalDateTime.now())
//                .bettingPoint()
//                .build();


        return false;
    }
}
