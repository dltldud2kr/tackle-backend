package com.example.tackle.voteResult.repository;

import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle.voteResult.entity.VoteResult;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface VoteResultRepository extends JpaRepository<VoteResult,Long> {

    Optional<VoteResult> findByIdxAndItemId(String idx,Long itemId);
    Optional<VoteResult> findByPostIdAndIdx(Long postId, String memberIdx);
    Optional<VoteResult> findByIdxAndPostId(String idx, Long postId);

    List<VoteResult> findAllByIdx(String memberIdx);

    List<VoteResult> findByPostIdAndStatus(Long postId, VotingResultStatus status);
    List<VoteResult> findByPostId(Long postId);


    Optional<VoteResult> findByResultIdAndIdx(Long resultId , String memberIdx);
}
