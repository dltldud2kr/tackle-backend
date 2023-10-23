package com.example.tackle.votingBoard.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.votingBoard.VotingBoardDto;
import com.example.tackle.votingBoard.entity.VotingBoard;
import com.example.tackle.votingBoard.repository.VotingBoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotingBoardServiceImpl implements VotingBoardService {

    private final VotingBoardRepository votingBoardRepository;
    private final MemberRepository memberRepository;

    @Override
    public boolean create(VotingBoardDto dto) {

        memberRepository.findById(dto.getIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));


            VotingBoard votingBoard = VotingBoard.builder()
                    .categoryId(dto.getCategoryId())
                    .bettingAmount(dto.getBettingAmount())
                    .content(dto.getContent())
                    .createdAt(LocalDateTime.now())
                    .votingResult(null)
                    .endDate(null)
                    .idx(dto.getIdx())
                    .votingImgUrl(dto.getVotingImgUrl())
                    .title(dto.getTitle())
                    .votingResult(null)
                    .status(null)
                    .build();

            votingBoardRepository.save(votingBoard);


        return true;
    }

    @Override
    public List<VotingBoardDto> getBoardList() {

        List<VotingBoard> votingBoard = votingBoardRepository.findAll();

        List<VotingBoardDto> votingBoardDtoList = of(votingBoard);


        return votingBoardDtoList;
    }

    @Override
    public VotingBoardDto getBoardInfo(long boardId) {


        VotingBoard votingBoard = votingBoardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        System.out.println("boardId: " + boardId);
        System.out.println(votingBoard.getTitle());

        VotingBoardDto dto  = VotingBoardDto.builder()
                .categoryId(votingBoard.getCategoryId())
                .bettingAmount(votingBoard.getBettingAmount())
                .content(votingBoard.getContent())
                .title(votingBoard.getTitle())
                .idx(votingBoard.getIdx())
                .createdAt(votingBoard.getCreatedAt())
                .endDate(votingBoard.getEndDate())
                .postId(votingBoard.getPostId())
                .votingResult(votingBoard.getVotingResult())
                .status(votingBoard.getStatus())
                .votingImgUrl(votingBoard.getVotingImgUrl())
                .build();
        return dto;
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
                .status(votingBoard.getStatus())
                .votingResult(votingBoard.getVotingResult())
                .content(votingBoard.getContent())
                .title(votingBoard.getTitle())
                .idx(votingBoard.getIdx())
                .endDate(votingBoard.getEndDate())
                .postId(votingBoard.getPostId())
                .bettingAmount(votingBoard.getBettingAmount())
                .createdAt(votingBoard.getCreatedAt())
                .build();
    }




}
