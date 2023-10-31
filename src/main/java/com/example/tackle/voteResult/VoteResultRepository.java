package com.example.tackle.voteResult;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VoteResultRepository extends JpaRepository<VoteResult,Long> {

    Optional<VoteResult> findByIdxAndItemId(String idx,Long itemId);
    Optional<VoteResult> findByPostIdAndIdx(Long postId, String memberIdx);

    List<VoteResult> findAllByIdx(String memberIdx);

    Optional<VoteResult> findByResultIdAndIdx(Long resultId , String memberIdx);
}
