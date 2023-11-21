package com.example.tackle.replies.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.replies.dto.RepliesDto;
import com.example.tackle.replies.entity.Replies;
import com.example.tackle.replies.repository.RepliesRepository;
import com.example.tackle.voteResult.entity.VoteResult;
import com.example.tackle.voteResult.repository.VoteResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepliesServiceImpl implements RepliesService {

    private final RepliesRepository repliesRepository;
    private final VoteResultRepository voteResultRepository;
    private final MemberRepository memberRepository;

    public boolean create(RepliesDto dto) {
        Member member = memberRepository.findById(dto.getIdx())
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        if (!voteOk(dto.getIdx(), dto.getPostId())) {
            throw new CustomException(CustomExceptionCode.NOT_VOTED);
        }

        Replies replies = Replies.builder()
                .idx(dto.getIdx())
                .nickname(member.getNickname())
                .postId(dto.getPostId())
                .comment(dto.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        repliesRepository.save(replies);
        return true;
    }
    public boolean voteOk(String idx, Long postId) {
        // 사용자가 해당 게시글에 투표 참여했는지 확인
        Optional<VoteResult> voteResult = voteResultRepository.findByIdxAndPostId(idx, postId);
        return voteResult.isPresent();
    }


    private List<RepliesDto> RepliesDtoList(List<Replies> repliesList) {
        LocalDateTime currentDateTime = LocalDateTime.now();

        return repliesList.stream()
                .map(replies -> RepliesToRepliesDto(replies, currentDateTime))
                .collect(Collectors.toList());
    }

    private RepliesDto RepliesToRepliesDto(Replies replies, LocalDateTime currentDateTime) {
        Duration duration = Duration.between(replies.getCreatedAt(), currentDateTime);
        long minutesAgo = duration.toMinutes();
        String timeAgo;

        if (minutesAgo >= 1440) {
            long daysAgo = duration.toDays();
            timeAgo = daysAgo + " days. ago";
        } else if (minutesAgo >= 60) {
            long hoursAgo = duration.toHours();
            timeAgo = hoursAgo + " hr. ago";
        } else {
            timeAgo = minutesAgo + " min. ago";
        }

        return RepliesDto.builder()
                .repliesId(replies.getRepliesId())
                .idx(replies.getIdx())
                .nickname(replies.getNickname())
                .postId(replies.getPostId())
                .comment(replies.getComment())
                .createdAt(replies.getCreatedAt())
                .createdMinutesAgo(timeAgo)
                .build();
    }

    @Override
    public List<RepliesDto> getRepliesInfo(Long postId) {
        List<Replies> repliesList = repliesRepository.findAllByPostId(postId);
        return RepliesDtoList(repliesList);
    }

    @Override
    public List<RepliesDto> getMyRepliesInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        List<Replies> myRepliesList = repliesRepository.findByIdx(member.getIdx());
        return RepliesDtoList(myRepliesList);
    }

    @Override
    public boolean delete(Long repliesId, String idx) {
        Optional<Replies> repliesOpt = repliesRepository.findById(repliesId);

        if (repliesOpt.isPresent()) {
            Replies replies = repliesOpt.get();

            // 댓글 작성자 ID와 현재 사용자 ID 비교
            if (replies.getIdx().equals(idx)) {
                repliesRepository.delete(replies);
                return true;
            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }

        }
        throw new CustomException(CustomExceptionCode.NOT_FOUND_REPLIES);

    }

    @Override
    public boolean update(Long repliesId, String idx, RepliesDto dto) {
        Replies replies = repliesRepository.findAllByRepliesIdAndIdx(repliesId, idx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_REPLIES));

        if (replies.getIdx().equals(idx)) {
            replies.setComment(dto.getComment());
            replies.setUpdatedAt(LocalDateTime.now());
            repliesRepository.save(replies);
            return true;
        }
        throw new RuntimeException();
    }



}