package com.example.tackle.voteResult.service;

import com.example.tackle.voteResult.entity.VoteResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteResultService {


    /**
     * 투표 참여 정보 조회
     */
    List<VoteResult> list(String email);

}
