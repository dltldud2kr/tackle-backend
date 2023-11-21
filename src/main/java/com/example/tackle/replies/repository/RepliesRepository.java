package com.example.tackle.replies.repository;

import com.example.tackle.replies.entity.Replies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepliesRepository extends JpaRepository<Replies, Long> {

    List<Replies> findAllByPostId(Long postId);
    List<Replies> findByIdx(String idx);

    Optional<Replies> findAllByRepliesIdAndIdx(Long repliesId, String idx);
}
