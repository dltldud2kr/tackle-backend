package com.example.tackle.votingBoard.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle._enum.VotingResultStatus;
import com.example.tackle._enum.VotingStatus;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.point.entity.Point;
import com.example.tackle.point.repository.PointRepository;
import com.example.tackle.point.service.PointService;
import com.example.tackle.revenue.RevenueRepository;
import com.example.tackle.revenue.RevenueService;
import com.example.tackle.voteItems.entity.VoteItems;
import com.example.tackle.voteItems.repository.VoteItemsRepository;
import com.example.tackle.voteItems.service.VoteItemsService;
import com.example.tackle.voteResult.dto.VoteResultDto;
import com.example.tackle.voteResult.entity.VoteResult;
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
import java.util.stream.Collectors;

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
    public Long create(VotingBoardDto dto) {


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
                .endDate(endDate)
                .nickname(member.getNickname())
                .idx(dto.getIdx())
                .votingImgUrl(dto.getVotingImgUrl())
                .title(dto.getTitle())
                .votingAmount(0L)
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

        revenueService.create(-BOARD_CREATE_COST,1);


        Long savedPostId = savedVotingBoard.getPostId();

        // 투표 항목 로직 (선택지 2개이상 선택)
        voteItemsService.create(savedPostId,dto.getVoteItemsContent());



        return savedPostId;
    }

    @Override
    public List<VotingBoardDto> getBoardListByCategory(Long categoryId) {
        if (categoryId != null) {
            // categoryId가 제공된 경우 해당 카테고리의 게시글만 조회
            return getBoardListByCategoryId(categoryId);
        } else {
            // categoryId가 제공되지 않은 경우 모든 게시글 조회
            return getBoardList();
        }
    }

    private List<VotingBoardDto> getBoardListByCategoryId(Long categoryId) {

        List<VotingBoard> votingBoard;

        if (categoryId == 0) {
            votingBoard = votingBoardRepository.findAllByOrderByVotingAmountDesc();
        } else {
            votingBoard = votingBoardRepository.findByCategoryId(categoryId);
        }

        List<VotingBoardDto> votingBoardList = new ArrayList<>();

        for (VotingBoard x : votingBoard) {
            List<VoteItems> voteItemsList = voteItemsRepository.findByPostId(x.getPostId());
            List<String> voteItemsContent = voteItemsList.stream()
                    .map(VoteItems::getContent)
                    .collect(Collectors.toList());

            VotingBoardDto dto = VotingBoardDto.builder()
                    .createdAt(x.getCreatedAt())
                    .categoryId(x.getCategoryId())
                    .voteItemsContent(voteItemsContent)
                    .postId(x.getPostId())
                    .idx(x.getIdx())
                    .status(x.getStatus())
                    .title(x.getTitle())
                    .votingDeadLine(x.getVotingDeadLine())
                    .votingImgUrl(x.getVotingImgUrl())
                    .votingResult(x.getVotingResult())
                    .nickname(x.getNickname())
                    .votingAmount(x.getVotingAmount())
                    .bettingAmount(x.getTotalBetAmount())
                    .content(x.getContent())
                    .endDate(x.getEndDate())
                    .build();

            votingBoardList.add(dto);
        }

        return votingBoardList;
    }

    @Override
    public List<VotingBoardDto> getBoardList() {
        List<VotingBoard> votingBoards = votingBoardRepository.findAll();


        List<VotingBoardDto> votingBoardList = new ArrayList<>();

        for (VotingBoard x : votingBoards) {
            List<String> voteItems = new ArrayList<>();
            List<VoteItems> voteItemsList = voteItemsRepository.findByPostId(x.getPostId());

            // 투표 항목 String 값
            for (VoteItems y : voteItemsList){
                voteItems.add(y.getContent());
            }
            VotingBoardDto dto = VotingBoardDto.builder()
                    .createdAt(x.getCreatedAt())
                    .categoryId(x.getCategoryId())
                    .voteItemsContent(voteItems)
                    .postId(x.getPostId())
                    .idx(x.getIdx())
                    .status(x.getStatus())
                    .title(x.getTitle())
                    .votingDeadLine(x.getVotingDeadLine())
                    .votingImgUrl(x.getVotingImgUrl())
                    .votingResult(x.getVotingResult())
                    .nickname(x.getNickname())
                    .votingAmount(x.getVotingAmount())
                    .bettingAmount(x.getTotalBetAmount())
                    .content(x.getContent())
                    .endDate(x.getEndDate())
                    .build();
            votingBoardList.add(dto);
        }
        return votingBoardList;
    }

    @Override
    public List<VotingBoardDto> getMyBoardList(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));


       List<VotingBoard> votingBoardList = votingBoardRepository.findByIdx(member.getIdx());

        return of(votingBoardList);
    }


    @Override
    public VotingBoardResponseDto getBoardInfo(Long postId, String email) {

        boolean isVoting = false;
        String id = "";
//        String nickname = "";
        if(!email.isEmpty()){
            Optional<Member> member  = memberRepository.findByEmail(email);
            id = member.get().getIdx();
//            nickname = member.get().getNickname();

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

        // 조회시 투표결과 업데이트
        if (!(votingBoard.getStatus() == VotingStatus.END)){
            //게시글 상태 업데이트
            boolean result = updateVotingStatusIfNeeded(votingBoard);


            if(result == false){
                //투표자 승패 업데이트
                voterWL(postId);
                long totalAmount = votingBoard.getTotalBetAmount();
                // 베팅한 사람들에게 포인트 분배
                distributePoint(postId,totalAmount);
            }

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
                .nickname(votingBoard.getNickname())
//                .voteItemsId(itemIds)
                .voteItemsContent(itemContents)
                .endDate(votingBoard.getEndDate())
                .postId(votingBoard.getPostId())
                .votingResult(votingBoard.getVotingResult())
                .status(votingBoard.getStatus())
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
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_ITEMS));

        Long postId = voteItems.getPostId();

        // 해당 투표 게시글 존재여부 확인
        VotingBoard votingBoard = votingBoardRepository.findById(postId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_BOARD));

        //투표를 이미 했는지 확인
        Optional<VoteResult> existingVoteResult = voteResultRepository.findByIdxAndItemId(dto.getIdx(),dto.getItemId());
        if (existingVoteResult.isPresent()) {

            // 이미 투표를 한 경우에 대한 예외 처리
            throw new CustomException(CustomExceptionCode.ALREADY_VOTED);
        }

        // 투표 기간이 지났는지 확인
        if(votingBoard.getStatus() == VotingStatus.END) {

            throw new CustomException(CustomExceptionCode.EXPIRED_VOTE);
        } else {

            /**
             * 투표 기한이 지났는지 확인  [ 메서드 ]
             */
            boolean result = updateVotingStatusIfNeeded(votingBoard);

            //기한이 지났으면 false
            if(result == false){

                /**
                 * 투표자들 승패 결정 [ 메서드 ]
                 */
                voterWL(dto.getPostId());
                // 총 게시글 금액
                long totalAmount = votingBoard.getTotalBetAmount();
                /**
                 * 투표 결과에 따른 포인트 지급 [ 메서드 ]
                 */
                distributePoint(postId,totalAmount);

                throw new CustomException(CustomExceptionCode.EXPIRED_VOTE);
            }
        }

        // 베팅을 할 수 있는 포인트가 있는지 확인
        if (member.getPoint() < dto.getBettingPoint()){
            throw new CustomException(CustomExceptionCode.NOT_ENOUGH_POINTS);
        }

        /**
         * 투표시 베팅한 금액 체크 10000, 50000, 100000 제한    [ 메서드 ]
         */
        boolean bettingValid = isBettingAmountValid(dto.getBettingPoint());

        if(!bettingValid){
            throw new CustomException(CustomExceptionCode.INVALID_BETTING_AMOUNT);

        }

        votingBoard.setTotalBetAmount(votingBoard.getTotalBetAmount() + dto.getBettingPoint());

        VoteResult voteResult = VoteResult.builder()
                .bettingPoint(dto.getBettingPoint())    // 10000, 50000 , 100000 제한  Exception 필요
                .itemId(dto.getItemId())
                .postId(dto.getPostId())
                .status(VotingResultStatus.ING)
                .createdAt(LocalDateTime.now())
                .getPoint(0L)
                .idx(dto.getIdx())
                .build();
        voteResultRepository.save(voteResult);

        //포인트 테이블 추가
        pointService.create(dto.getIdx(), -dto.getBettingPoint(), 3);
        // 멤버 테이블 포인트 수정
        member.setPoint(member.getPoint() - dto.getBettingPoint());
        memberRepository.save(member);
        //투표 수 증가
        voteItems.setVoteCount(voteItems.getVoteCount() + 1);
        voteItemsRepository.save(voteItems);
        // 게시글 총 투표수 증가
        votingBoard.setVotingAmount(votingBoard.getVotingAmount() + 1);
        votingBoardRepository.save(votingBoard);


        return true;
    }

    @Override
    public boolean delete(String email, Long postId) {
        if(!email.isEmpty()) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

            if(member.getRole() == 1){
                if (votingBoardRepository.existsById(postId)) {

                    votingBoardRepository.deleteById(postId);
                    return true;
                } else{
                    throw new CustomException(CustomExceptionCode.NOT_FOUND_BOARD);
                }

            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        } else{
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }

    }

    @Override
    public List<VotingBoard> search(String keyword) {
        return votingBoardRepository.findByTitleContaining(keyword);
    }


    /**
     * 투표자 승패 업데이트 메서드
     * @param boardId
     */
    public void voterWL (Long boardId){

        List<VoteResult> voteResultList = voteResultRepository.findByPostId(boardId);
        Optional<VotingBoard> votingBoard = votingBoardRepository.findById(boardId);


        for (VoteResult voteResult : voteResultList){
//            Long postId = voteResult.getPostId();
            List<VoteItems> voteItemsDesc = voteItemsRepository.findByPostIdOrderByVoteCountDesc(boardId);

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

                // minCountItems 리스트에는 가장 적은 카운트를 가진 VoteItems 객체들이 저장.
                // 이 리스트는 동일한 카운트를 가진 항목들을 모두 포함.

                if (minCountItems.size() == 1) {
                    // 동일한 카운트가 없는 경우 다른 메소드를 실행
                    VoteItems voteItems2 = minCountItems.get(0);

                    Long voteItemResult = voteItems2.getItemId();
                    Long userVoteResult = voteResult.getItemId();

                    votingBoard.get().setVotingResult(voteItemResult);

                    if (voteItemResult.equals(userVoteResult)) {
                        voteResult.setStatus(VotingResultStatus.LOSE);
                    } else {
                        voteResult.setStatus(VotingResultStatus.WIN);
                    }
                } else {
                    // 동일한 카운트가 있는 경우 다른 처리를 수행하거나 필요한 로직을 추가
                    // minCountItems에는 동일한 카운트를 가진 항목들이 포함됨.
                    // 다른 처리를 수행하는 로직을 여기에 추가.
                    voteResult.setStatus(VotingResultStatus.DRAW);
                }

                // 엔터티 상태를 변경한 후 저장
                voteResultRepository.save(voteResult);

            } else {
                throw new CustomException(CustomExceptionCode.NOT_FOUND,"투표항목이 없습니다.");
            }
        }
    }

    /**
     * 투표 기간이 지났으면 투표상태를 END로 변경 로직
     * @param votingBoard
     * @return
     */
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

    /**
     * 투표시 베팅한 금액 체크 1000, 5000, 10000 제한
     * @param bettingPoint
     * @return
     */
    public boolean isBettingAmountValid(Long bettingPoint) {
        return bettingPoint == 1000 || bettingPoint == 5000 || bettingPoint == 10000;
    }

    /**
     * 포인트 분배 메서드
     * @param postId
     * @param totalAmount
     */
    public void distributePoint(Long postId, Long totalAmount){
        // 수수료를 공제한 총 상금 베팅금
        long totalPrize = totalAmount;
        double commission = totalPrize *  0.03; //수수료 계산 3%
        double netPrize = totalPrize - commission;

        // 수수료 제외 베팅금 내림처리
        double remainingAmount = Math.floor(netPrize);

        List<VoteResult> voteResultList = voteResultRepository.findByPostId(postId);

        // 베팅에 승리한 사람들의 총 포인트
        long totalWinAmount = 0;

        //DRAW : false  // WIN : true
        boolean result = false;
        int count = 0;

        for(VoteResult x : voteResultList){
            // 승리한 사람만 필터링
            if ((x.getStatus() == VotingResultStatus.WIN) && (x.getBettingPoint() > 0)){

                totalWinAmount += x.getBettingPoint();
                result = true;

                //무승부만 필터링
            } else if ((x.getStatus() == VotingResultStatus.DRAW) && (x.getBettingPoint() > 0)){
                count++;
            }

        }
        // double 형태로 변경 ( 승리 베팅자 포인트 총합 )
        double totalWinAmountDouble = (double) totalWinAmount;

        //버림 처리 한 총 금액
        double floorTotalAmount = 0;
        double userShare = 0;

        for(VoteResult x : voteResultList){
            if(result && (x.getStatus() == VotingResultStatus.WIN)){

                userShare = Math.floor((x.getBettingPoint() / totalWinAmountDouble) * remainingAmount);

            } else if (!result) {

                userShare = Math.floor(remainingAmount / count);
            }

            // 베팅결과에 따른 포인트지급 (승리)
            // LOSE 빼고 포인트 적립
            if(!(x.getStatus() == VotingResultStatus.LOSE)){

                //얻은 포인트 DB에서 수정
                x.setGetPoint((long)userShare);

                floorTotalAmount +=userShare;

                Member member= memberRepository.findById(x.getIdx())
                        .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

                member.setPoint(member.getPoint() + (long)userShare);

                //회원 잔여 포인트 수정
                memberRepository.save(member);

                //포인트 내역에 저장
                pointService.create(x.getIdx(),(long)userShare,1);
            }

        }

        // 버림 처리로 생긴 남은 포인트
        double floorRemainPoint = netPrize -floorTotalAmount;
        // 최종 Tackle 이 얻은 포인트
        Long finallyPoint = (long) (commission + floorRemainPoint);

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
                .status(votingBoard.getStatus())
                .votingResult(votingBoard.getVotingResult())
                .content(votingBoard.getContent())
                .title(votingBoard.getTitle())
                .nickname(votingBoard.getNickname())
                .idx(votingBoard.getIdx())
                .endDate(votingBoard.getEndDate())
                .postId(votingBoard.getPostId())
                .bettingAmount(votingBoard.getTotalBetAmount())
                .createdAt(votingBoard.getCreatedAt())
                .build();
    }


}


