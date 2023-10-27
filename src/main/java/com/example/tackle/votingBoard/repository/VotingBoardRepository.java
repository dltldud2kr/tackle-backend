package com.example.tackle.votingBoard.repository;

import com.example.tackle.votingBoard.entity.VotingBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotingBoardRepository extends JpaRepository<VotingBoard, Long> {
}
