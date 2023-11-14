package com.example.tackle.voteResult.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle._enum.VotingStatus;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
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

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        String id= "";
        if(!email.isEmpty()) {
            id = member.getIdx();
        }

        List<VoteResult> voteResultList = voteResultRepository.findAllByIdx(id);

        for (VoteResult voteResult : voteResultList){


            Long postId = voteResult.getPostId();
            VotingBoard votingBoard = votingBoardRepository.findById(postId)
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

            if (votingBoard.getStatus() == VotingStatus.ING){
                System.out.println("INGpostId :" + voteResult.getPostId());
                System.out.println("INGidx : " + voteResult.getIdx());
                System.out.println("INGitemId : " + voteResult.getItemId());
                System.out.println("INGstatus : " + voteResult.getStatus());
            }


            if (votingBoard.getStatus() != VotingStatus.END){
                System.out.println("end in");
                System.out.println("=========================================");
                System.out.println("postId : " + voteResult.getPostId());

                System.out.println("status : " + votingBoard.getStatus());

                //게시글 상태 업데이트
                boolean result = votingBoardServiceImpl.updateVotingStatusIfNeeded(votingBoard);
                System.out.println("result 실행 후");

                //투표자 승패 업데이트

                if (result == false){
                    System.out.println("false에 들어왔음");
                    System.out.println("postId : " + voteResult.getPostId());
                    System.out.println("result in");

                    //투표자 승패 업데이트
                    votingBoardServiceImpl.voterWL(voteResult.getPostId());
                    System.out.println("voter In");

                    long totalAmount = votingBoard.getTotalBetAmount();
                    // 베팅한 사람들에게 포인트 분배
                    votingBoardServiceImpl.distributePoint(voteResult.getPostId(),totalAmount);
                }
            }
            System.out.println("마지막");
            System.out.println(voteResult.getIdx());
            System.out.println(voteResult.getStatus());
            }


        return voteResultList;

        }

}
