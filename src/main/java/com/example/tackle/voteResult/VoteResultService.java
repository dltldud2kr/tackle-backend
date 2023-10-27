package com.example.tackle.voteResult;

import org.springframework.stereotype.Service;

@Service
public interface VoteResultService {
    /**
     * 투표 결과 생성
     * @return
     */
    boolean register(Long itemId);

}
