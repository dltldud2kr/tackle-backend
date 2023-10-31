package com.example.tackle.voteResult;

import org.springframework.stereotype.Service;

import java.util.List;

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
    boolean list2(Long resultId, String email);

    List<VoteResult> list(String email);

}
