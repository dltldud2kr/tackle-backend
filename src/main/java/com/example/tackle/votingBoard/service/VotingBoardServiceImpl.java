package com.example.tackle.votingBoard.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle._enum.VotingStatus;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.voteItems.entity.VoteItems;
import com.example.tackle.voteItems.repository.VoteItemsRepository;
import com.example.tackle.voteItems.service.VoteItemsService;
import com.example.tackle.voteResult.entity.VoteResult;
import com.example.tackle.voteResult.dto.VoteResultDto;
import com.example.tackle.voteResult.repository.VoteResultRepository;
import com.example.tackle.votingBoard.dto.VotingBoardDto;
import com.example.tackle.votingBoard.dto.VotingBoardResponseDto;
import com.example.tackle.votingBoard.entity.VotingBoard;
import com.example.tackle.votingBoard.repository.VotingBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingBoardServiceImpl implements VotingBoardService {

    private final VoteItemsService voteItemsService;
    private final VotingBoardRepository votingBoardRepository;
    private final MemberRepository memberRepository;

    private final VoteItemsRepository voteItemsRepository;

    private final VoteResultRepository voteResultRepository;

    @Transactional
    public boolean create(VotingBoardDto dto) {

        memberRepository.findById(dto.getIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        LocalDateTime currentDateTime = LocalDateTime.now(); // 현재 시각 가져오기

        byte votingDeadLineDays = dto.getVotingDeadLine(); // 투표 마감 기한(1~7일)

        if(votingDeadLineDays > 7 || votingDeadLineDays < 1){
            throw new CustomException(CustomExceptionCode.INVALID_DEADLINE);
        }

        // 현재 시각에서 votingDeadLineDays 일을 더해 endDate를 계산
        LocalDateTime endDate = currentDateTime.plus(votingDeadLineDays, ChronoUnit.DAYS);


        VotingBoard votingBoard = VotingBoard.builder()
                    .categoryId(dto.getCategoryId())
                    .totalBetAmount(0L)
                    .content(dto.getContent())
                    .createdAt(currentDateTime)
                    .votingResult(VotingResultStatus.ING)
                    .endDate(endDate)
                    .idx(dto.getIdx())
                    .votingImgUrl(dto.getVotingImgUrl())
                    .title(dto.getTitle())
                    .status(VotingStatus.ING)
                    .votingDeadLine(votingDeadLineDays)
                    .build();

            VotingBoard savedVotingBoard = votingBoardRepository.save(votingBoard);
            Long savedPostId = savedVotingBoard.getPostId();

            // 투표 항목 로직 (선택지 2개이상 선택)
            voteItemsService.create(savedPostId,dto);



        return true;
    }

    @Override
    public List<VotingBoardDto> getBoardList() {

        List<VotingBoard> votingBoard = votingBoardRepository.findAll();

        List<VotingBoardDto> votingBoardDtoList = of(votingBoard);


        return votingBoardDtoList;
    }

    @Override
    public VotingBoardResponseDto getBoardInfo(Long postId, String id) {

        System.out.println("postId = " + postId);
        System.out.println("id = " + id);
        boolean isVoting = false;

        VotingBoard votingBoard = votingBoardRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        List<VoteItems> voteItemsList = voteItemsRepository.findByPostId(postId);

        //투표항목이 없는 게시글 예외처리
        if(voteItemsList.isEmpty()){
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ITEMS);
        }

        System.out.println("Test");
        Optional<VoteResult> voteResult = voteResultRepository.findByPostIdAndIdx(postId, id);
        if (voteResult.isPresent()){
            isVoting = true;
        }

        System.out.println("isVoting : " + isVoting);
        //해당 게시글의 투표항목리스트를 가져옴
        List<Long> itemIds = new ArrayList<>();
        List<String> itemContents = new ArrayList<>();

        for (VoteItems voteItem : voteItemsList) {
            itemIds.add(voteItem.getItemId());
            itemContents.add(voteItem.getContent());
        }
        //투표 기한이 지났는지 확인
        updateVotingStatusIfNeeded(votingBoard);

        System.out.println("boardId: " + postId);
        System.out.println(votingBoard.getTitle());

        VotingBoardResponseDto dto  = VotingBoardResponseDto.builder()
                .categoryId(votingBoard.getCategoryId())
                .bettingAmount(votingBoard.getTotalBetAmount())
                .content(votingBoard.getContent())
                .title(votingBoard.getTitle())
                .idx(votingBoard.getIdx())
                .createdAt(votingBoard.getCreatedAt())
                .voteItemsId(itemIds)
                .voteItemsContent(itemContents)
                .endDate(votingBoard.getEndDate())
                .postId(votingBoard.getPostId())
                .votingResult(votingBoard.getVotingResult())
                .status(votingBoard.getStatus().toString())
                .isVoting(isVoting)
                .votingImgUrl(votingBoard.getVotingImgUrl())
                .build();



        return dto;
    }



    @Override
    public boolean voting(VoteResultDto dto) {

        // 해당 투표항목이 존재여부 확인
        VoteItems voteItems = voteItemsRepository.findById(dto.getItemId())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        Long postId = voteItems.getPostId();

        // 해당 투표 게시글 존재여부 확인
        VotingBoard votingBoard = votingBoardRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        //투표를 이미 했는지 확인
        Optional<VoteResult> existingVoteResult = voteResultRepository.findByIdxAndItemId(dto.getIdx(),dto.getItemId());
        if (existingVoteResult.isPresent()) {

            // 이미 투표를 한 경우에 대한 예외 처리
            throw new CustomException(CustomExceptionCode.ALREADY_VOTED);
        }

        //투표기한이 지났는지 확인
        boolean result = updateVotingStatusIfNeeded(votingBoard);

        if(result == false){
            throw new CustomException(CustomExceptionCode.EXPIRED_VOTE);
        }

        VoteResult voteResult = VoteResult.builder()
                .bettingPoint(dto.getBettingPoint())    // 10000, 50000 , 100000 제한  Exception 필요
                .itemId(dto.getItemId())
                .postId(dto.getPostId())
                .status(votingBoard.getVotingResult())
                .createdAt(LocalDateTime.now())
                .getPoint(0L)
                .idx(dto.getIdx())
                .build();
        voteResultRepository.save(voteResult);

        //투표 수 증가
        voteItems.setVoteCount(voteItems.getVoteCount() + 1);
        voteItemsRepository.save(voteItems);


        return true;
    }




    // 투표 기간이 지났으면 투표상태를 END로 변경 로직
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


    public  List<VotingBoardDto> of (List<VotingBoard> votingBoards) {

        if (votingBoards == null) {
            return null;
        }

        List<VotingBoardDto> votingBoardList = new ArrayList<>();
        for (VotingBoard x : votingBoards) {
            votingBoardList.add(of(x));
        }
        return votingBoardList;

    }

    public  VotingBoardDto of(VotingBoard votingBoard){


        return VotingBoardDto.builder()

                .categoryId(votingBoard.getCategoryId())
                .votingImgUrl(votingBoard.getVotingImgUrl())
                .status(votingBoard.getStatus().toString())
                .votingResult(votingBoard.getVotingResult())
                .content(votingBoard.getContent())
                .title(votingBoard.getTitle())
                .idx(votingBoard.getIdx())
                .endDate(votingBoard.getEndDate())
                .postId(votingBoard.getPostId())
                .bettingAmount(votingBoard.getTotalBetAmount())
                .createdAt(votingBoard.getCreatedAt())
                .build();
    }




}
