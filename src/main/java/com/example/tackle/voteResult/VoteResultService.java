package com.example.tackle.voteResult;

import org.springframework.stereotype.Service;

@Service
public interface VoteResultService {
    /**
     * 투표 결과 생성
     * @return
     */
    boolean register(Long itemId);

    /**
     * 투표 참여 정보 조회
     */
    boolean info(Long resultId, String memberIdx);

}
