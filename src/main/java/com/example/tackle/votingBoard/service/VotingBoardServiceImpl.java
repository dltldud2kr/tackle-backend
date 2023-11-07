package com.example.tackle.votingBoard.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle._enum.VotingStatus;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.point.Point;
import com.example.tackle.point.PointRepository;
import com.example.tackle.point.PointService;
import com.example.tackle.revenue.RevenueRepository;
import com.example.tackle.revenue.RevenueService;
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
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class VotingBoardServiceImpl implements VotingBoardService {
    private final PointRepository pointRepository;

    private final VoteItemsService voteItemsService;
    private final RevenueService revenueService;
    private final PointService pointService;
    private final VotingBoardRepository votingBoardRepository;
    private final MemberRepository memberRepository;

    private final VoteItemsRepository voteItemsRepository;

    private final VoteResultRepository voteResultRepository;
    private final RevenueRepository revenueRepository;

    private static final Long BOARD_CREATE_COST = -1000L;

    @Transactional
    public boolean create(VotingBoardDto dto) {


        Member member = memberRepository.findById(dto.getIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        //포인트가 부족하면 게시글 작성 불가
        if (member.getPoint() < -BOARD_CREATE_COST){
            throw new CustomException(CustomExceptionCode.NOT_ENOUGH_POINTS);
        }

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

            //게시글 작성시 1000P 차감
            member.setPoint(member.getPoint() + BOARD_CREATE_COST);
            memberRepository.save(member);

            // 포인트 사용내역 저장
            Point pointCreate = pointService.create(dto.getIdx(),BOARD_CREATE_COST, 0);

            pointRepository.save(pointCreate);


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
    public VotingBoardResponseDto getBoardInfo(Long postId, String email) {

        System.out.println("in");
        System.out.println(email);
        boolean isVoting = false;
        String id = "";
        if(!email.isEmpty()){
            id = memberRepository.findByEmail(email).get().getIdx();

            //로그인이 된 회원의 경우 투표를 한 게시글이면 isVoting 을 true로 반환해줌으로써
            // 투표화면이 아닌 투표비율이 뜨는 투표결과화면을 보여주게함.
            Optional<VoteResult> voteResult = voteResultRepository.findByPostIdAndIdx(postId, id);
            if (voteResult.isPresent()){
                isVoting = true;
            }
        }


        Map<Long, Long> voteItemIdMap = new HashMap<>();

        VotingBoard votingBoard = votingBoardRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        List<VoteItems> voteItemsList = voteItemsRepository.findByPostId(postId);

        //투표항목이 없는 게시글 예외처리
        if(voteItemsList.isEmpty()){
            throw new CustomException(CustomExceptionCode.NOT_FOUND_ITEMS);
        }



        //해당 게시글의 투표항목리스트를 가져옴
//        List<Long> itemIds = new ArrayList<>();
        List<String> itemContents = new ArrayList<>();

        for (VoteItems voteItem : voteItemsList) {
            Long itemId = voteItem.getItemId();
            Long itemCount = voteItem.getVoteCount();
            //투표항목 id 값과 투표 count 값을 Map 으로 저장.
            voteItemIdMap.put(itemId, itemCount);
//            itemIds.add(voteItem.getItemId());
            itemContents.add(voteItem.getContent());
        }

        //투표 기한이 지났는지 확인
        updateVotingStatusIfNeeded(votingBoard);



        VotingBoardResponseDto dto  = VotingBoardResponseDto.builder()
                .categoryId(votingBoard.getCategoryId())
                .bettingAmount(votingBoard.getTotalBetAmount())
                .content(votingBoard.getContent())
                .title(votingBoard.getTitle())
                .idx(votingBoard.getIdx())
                .createdAt(votingBoard.getCreatedAt())
                .voteItemIdMap(voteItemIdMap)
//                .voteItemsId(itemIds)
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

        Member member = memberRepository.findById(dto.getIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));
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
            System.out.println("투표함");

            // 이미 투표를 한 경우에 대한 예외 처리
            throw new CustomException(CustomExceptionCode.ALREADY_VOTED);
        }
        System.out.println("투표안함");

        // 투표 기간이 지났는지 확인
        if(votingBoard.getStatus() == VotingStatus.END) {

            throw new CustomException(CustomExceptionCode.EXPIRED_VOTE);
        } else {
            // 투표 기한이 지났는지 확인
            boolean result = updateVotingStatusIfNeeded(votingBoard);

            // 투표자들 승패 결정
            voterWL(dto.getPostId());

            // 총 게시글 금액
            long totalAmount = votingBoard.getTotalBetAmount();
            if(result == false){
                //투표 결과에 따른 포인트 지급
                System.out.println("투표기한지남");
                distributePoint(postId,totalAmount);

                // 사용자들 투표에 승리했는지안했는지 확인. 2023-11-03


                throw new CustomException(CustomExceptionCode.EXPIRED_VOTE);
            }
        }

        // 베팅을 할 수 있는 포인트가 있는지 확인
        if (member.getPoint() < dto.getBettingPoint()){
            throw new CustomException(CustomExceptionCode.NOT_ENOUGH_POINTS);
        }

        System.out.println("돈있음");


        // 투표시 베팅한 금액 체크 10000, 50000, 100000 제한
        boolean bettingValid = isBettingAmountValid(dto.getBettingPoint());



        if(!bettingValid){
            throw new CustomException(CustomExceptionCode.INVALID_BETTING_AMOUNT);

        }

        votingBoard.setTotalBetAmount(votingBoard.getTotalBetAmount() + dto.getBettingPoint());

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

        //포인트 테이블 추가
        pointService.create(dto.getIdx(), dto.getBettingPoint(), 3);
        // 멤버 테이블 포인트 수정
        member.setPoint(member.getPoint() - dto.getBettingPoint());
        memberRepository.save(member);
        //투표 수 증가
        voteItems.setVoteCount(voteItems.getVoteCount() + 1);
        voteItemsRepository.save(voteItems);


        return true;
    }

    @Override
    public boolean delete(String email, Long postId) {
        if(!email.isEmpty()) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

            if(member.getRole() == 1){
                if (votingBoardRepository.existsById(postId)) {

                    votingBoardRepository.deleteById(postId);
                    return true;
                } else{
                    throw new CustomException(CustomExceptionCode.NOT_FOUND);
                }

            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        } else{
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }

    }


    void voterWL (Long boardId){

        //해당 유저의 투표결과리스트를 들고온다 ?
        //해당 postId와 같은 투표결과리스트를 들고오고 거기서 비교해야하는 거 아닌가?
        List<VoteResult> voteResultList = voteResultRepository.findByPostId(boardId);


        for (VoteResult voteResult : voteResultList){
            Long postId = voteResult.getPostId();

        List<VoteItems> voteItemsDesc = voteItemsRepository.findByPostIdOrderByVoteCountDesc(postId);

        System.out.println(voteItemsDesc.size());
        for (VoteItems x : voteItemsDesc){
            System.out.println(x.getVoteCount());
            System.out.println(x.getContent());
        }

        if (!voteItemsDesc.isEmpty()) {
            Long minCount = voteItemsDesc.get(0).getVoteCount();
            List<VoteItems> minCountItems = new ArrayList<>();

            // 투표가 동률일 때  ( 선택지가 3개인경우 2개만 동률이면 그에 대한 로직도 만들어야함.
            for (VoteItems item : voteItemsDesc) {
                if (item.getVoteCount() == minCount) {
                    minCountItems.add(item);
                } else if (item.getVoteCount() < minCount) {
                    minCount = item.getVoteCount();
                    minCountItems.clear();
                    minCountItems.add(item);
                }
            }

            // minCountItems 리스트에는 가장 적은 카운트를 가진 VoteItems 객체들이 저장됩니다.
            // 이 리스트는 동일한 카운트를 가진 항목들을 모두 포함합니다.

            if (minCountItems.size() == 1) {
                // 동일한 카운트가 없는 경우 다른 메소드를 실행
                VoteItems voteItems2 = minCountItems.get(0);
                Long result1 = voteItems2.getItemId();
                System.out.println("result1 = " + result1);
                Long result2 = voteResult.getItemId();
                System.out.println("result2 = " + result2);

                if (result1.equals(result2)) {
                    voteResult.setStatus(VotingResultStatus.LOSE);
                    System.out.println("LOSE");
                } else {
                    voteResult.setStatus(VotingResultStatus.WIN);
                    System.out.println("WIN");
                }
            } else {
                // 동일한 카운트가 있는 경우 다른 처리를 수행하거나 필요한 로직을 추가
                // minCountItems에는 동일한 카운트를 가진 항목들이 포함됨.
                // 다른 처리를 수행하는 로직을 여기에 추가.
                voteResult.setStatus(VotingResultStatus.DRAW);
            }
        } else {
            throw new CustomException(CustomExceptionCode.NOT_FOUND,"투표항목이 없습니다.");
        }
    }
    }


    // 투표 기간이 지났으면 투표상태를 END로 변경 로직
    public boolean updateVotingStatusIfNeeded(VotingBoard votingBoard) {

        LocalDateTime endDate = votingBoard.getEndDate();
        LocalDateTime currentDateTime = LocalDateTime.now();

        // 기간이 만료된 경우 투표상태를 END 로 변경
        if (endDate != null && endDate.isBefore(currentDateTime)) {
            votingBoard.setStatus(VotingStatus.END);
            votingBoardRepository.save(votingBoard);

            return false;
        }
        return true;
    }

    // 투표시 베팅한 금액 체크 10000, 50000, 100000 제한
    public boolean isBettingAmountValid(Long bettingPoint) {
        // 투표 금액의 유효성을 검사합니다.
        if (bettingPoint == 10000 || bettingPoint == 50000 || bettingPoint == 100000) {
            // 유효한 금액인 경우
            return true;
        } else {
            // 유효하지 않은 금액인 경우
            return false;
        }
    }

    public void distributePoint(Long postId, Long totalAmount){
        long totalPrize = totalAmount;
        //수수료 계산 3%
        double commission = totalPrize *  0.03;
        System.out.println("commission = " + commission);
        double netPrize = totalPrize - commission; // 수수료 공제 후 순수 베팅금
        System.out.println("netPrize = " + netPrize);

        // 순수 베팅금 버림처리
        double remainingAmount = Math.floor(netPrize);

        List<VoteResult> voteResultList = voteResultRepository.findByPostId(postId);

        // 베팅에 승리한 사람들의 총 포인트
        long totalWinAmount = 0;
        for(VoteResult x : voteResultList){
            // 승리한 사람만 필터링
            if (x.getStatus() == VotingResultStatus.WIN){
                System.out.println("유저이름 : " + x.getIdx());
                System.out.println("유저 상태 : " + x.getStatus());
                System.out.println("로직상태확인" + (x.getStatus() == VotingResultStatus.WIN));
                totalWinAmount += x.getBettingPoint();
                System.out.println("totalWinAmount = " + totalAmount);
            }

        }
        // double 형태로 변경
        double totalWinAmountDouble = totalWinAmount;
        System.out.println("totalWinAmountDouble(더블형태로변경) = " + totalWinAmountDouble);

        //버림 처리 한 총 금액
        double floorTotalAmount = 0;
        for(VoteResult x : voteResultList){
            double userShare = Math.floor((x.getBettingPoint() / totalWinAmountDouble) * remainingAmount);
            System.out.println(x.getBettingPoint() + "/" + totalWinAmountDouble + "*" + remainingAmount +
                    "= " + userShare);

            x.setGetPoint((long)userShare);

            System.out.println("setGetPoint = " + x.getGetPoint());

            floorTotalAmount +=userShare;

            System.out.println("floorTotalAmount = " + floorTotalAmount);

            // 베팅결과에 따른 포인트지급  (승리)
            Member member= memberRepository.findById(x.getIdx())
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

            member.setPoint(member.getPoint() + (long)userShare);

            System.out.println("member.getPoint = " + member.getPoint());

            memberRepository.save(member);

            //포인트 내역에 저장
            pointService.create(x.getIdx(),(long)userShare,1);
        }

        // 버림 처리로 생긴 남은 포인트
        double floorRemainPoint = netPrize -floorTotalAmount;

        System.out.println("floorRemainPoint = " + floorRemainPoint);

        // 최종 Tackle 이 얻은 포인트
        Long finallyPoint = (long) (commission + floorRemainPoint);

        System.out.println("finallyPoint = " + finallyPoint);

        revenueService.create(finallyPoint,0);

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
