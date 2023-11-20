package com.example.tackle.votingBoard.repository;

import com.example.tackle.votingBoard.entity.VotingBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VotingBoardRepository extends JpaRepository<VotingBoard, Long> {

    @Query("SELECT vb FROM VotingBoard vb WHERE vb.endDate <= :currentDateTime")
    List<VotingBoard> findExpiredVotingBoards(LocalDateTime currentDateTime);


    List<VotingBoard> findByCategoryId(Long categoryId);
}
