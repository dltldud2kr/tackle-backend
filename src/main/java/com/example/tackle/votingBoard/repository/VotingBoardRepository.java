package com.example.tackle.votingBoard.repository;

import com.example.tackle.votingBoard.entity.VotingBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VotingBoardRepository extends JpaRepository<VotingBoard, Long> {
}
