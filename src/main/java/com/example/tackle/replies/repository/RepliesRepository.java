package com.example.tackle.replies.repository;

import com.example.tackle.replies.entity.Replies;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepliesRepository extends JpaRepository<Replies, Long> {

    List<Replies> findAllByPostId(Long postId);
    List<Replies> findAllByIdx(Long idx);
}
