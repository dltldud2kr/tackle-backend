package com.example.tackle.voteItems.repository;

import com.example.tackle.member.entity.Member;
import com.example.tackle.voteItems.entity.VoteItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteItemsRepository extends JpaRepository<VoteItems, Long> {


}
