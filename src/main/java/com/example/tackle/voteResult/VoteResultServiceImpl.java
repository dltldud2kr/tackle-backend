package com.example.tackle.voteResult;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle._enum.VotingStatus;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.voteItems.entity.VoteItems;
import com.example.tackle.voteItems.repository.VoteItemsRepository;
import com.example.tackle.votingBoard.entity.VotingBoard;
import com.example.tackle.votingBoard.repository.VotingBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VoteResultServiceImpl implements VoteResultService {
    private final MemberRepository memberRepository;
    private final VoteResultRepository voteResultRepository;
    private final VoteItemsRepository voteItemsRepository;
    private final VotingBoardRepository votingBoardRepository;


    @Override
    public boolean register(Long itemId) {
        VoteItems voteItems = voteItemsRepository.findById(itemId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

//        VoteResult voteResult = VoteResult.builder()
//                .createdAt(LocalDateTime.now())
//                .bettingPoint()
//                .build();


        return false;
    }


    @Override
    public boolean info(Long resultId, String memberEmail) {

        String memberIdx = memberRepository.findByEmail(memberEmail).get().getIdx();

        VoteResult voteResult = voteResultRepository.findByResultIdAndIdx(resultId, memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        Long postId = voteResult.getPostId();

        VotingBoard votingBoard = votingBoardRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        boolean result = updateVotingStatusIfNeeded(votingBoard);


        // 게시글의 투표항목에서 투표수를 비교해 높은 투표항목를 가져옴
        // 높은 투표항목과 내 투표항목을 비교해 결과 출력
        if (result == false){


            List<VoteItems> voteItems3 = voteItemsRepository.findByPostIdOrderByVoteCountDesc(postId);

            // 투표가 동률일 경우 무승부
            if (voteItems3.size() >= 2){
                System.out.println("draw");

                voteResult.setStatus(VotingResultStatus.DRAW);
                voteResultRepository.save(voteResult);

                return true;

            }

            VoteItems voteItems2 = voteItems3.get(0);

            Long result1 = voteItems2.getItemId();
            Long result2 = voteResult.getItemId();

            if (result1.equals(result2)){
                voteResult.setStatus(VotingResultStatus.WIN);
                System.out.println("win");
            } else {
                voteResult.setStatus(VotingResultStatus.LOSE);
                System.out.println("lose");
            }

        }
        voteResultRepository.save(voteResult);


        return true;
    }



    private boolean updateVotingStatusIfNeeded(VotingBoard votingBoard) {
        LocalDateTime endDate = votingBoard.getEndDate();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (endDate != null && endDate.isBefore(currentDateTime)) {
            votingBoard.setStatus(VotingStatus.END);
            votingBoardRepository.save(votingBoard);

            return false;
        }
        return true;
    }

}
