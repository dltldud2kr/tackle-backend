package com.example.tackle.replies.service;

import com.example.tackle.replies.dto.RepliesDto;
import com.example.tackle.voteResult.dto.VoteResultDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RepliesService {

    boolean create(RepliesDto dto);

    List<RepliesDto> getRepliesInfo(Long postId);

    List<RepliesDto> getMyRepliesInfo(Long idx);

    boolean delete(Long repliesId, Long idx);

    boolean update(Long repliesId, Long idx, RepliesDto dto);
}
