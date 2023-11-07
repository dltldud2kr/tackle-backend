package com.example.tackle.voteResult.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle._enum.VotingStatus;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.voteItems.repository.VoteItemsRepository;
import com.example.tackle.voteResult.entity.VoteResult;
import com.example.tackle.voteResult.repository.VoteResultRepository;
import com.example.tackle.votingBoard.entity.VotingBoard;
import com.example.tackle.votingBoard.repository.VotingBoardRepository;
import com.example.tackle.votingBoard.service.VotingBoardServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteResultServiceImpl implements VoteResultService {
    private final MemberRepository memberRepository;
    private final VoteResultRepository voteResultRepository;
    private final VoteItemsRepository voteItemsRepository;
    private final VotingBoardRepository votingBoardRepository;
    private final VotingBoardServiceImpl votingBoardServiceImpl;




    @Override
    public List<VoteResult> list(String email) {

        String id= "";
        if(!email.isEmpty()) {
            id = memberRepository.findByEmail(email).get().getIdx();
        }

        List<VoteResult> voteResultList = voteResultRepository.findAllByIdx(id);

        for (VoteResult voteResult : voteResultList){

            Long postId = voteResult.getPostId();
            VotingBoard votingBoard = votingBoardRepository.findById(postId)
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));


            if (!(votingBoard.getStatus() == VotingStatus.END)){
                //게시글 상태 업데이트
                boolean result = votingBoardServiceImpl.updateVotingStatusIfNeeded(votingBoard);

                //투표자 승패 업데이트
                votingBoardServiceImpl.voterWL(voteResult.getPostId());

                long totalAmount = votingBoard.getTotalBetAmount();
                if (result == false){
                    // 베팅한 사람들에게 포인트 분배
                    votingBoardServiceImpl.distributePoint(voteResult.getPostId(),totalAmount);
                }
            }
            }

        return voteResultList;

        }

}
