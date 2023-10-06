package com.example.tackle;

import com.example.tackle.dto.loginRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;





    @Override
    public ResponseEntity join(MemberJoinRequest dto) {
        memberRepository.findByUserId(dto.getUserId())
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED , dto.getUserId() + "is already there.");
                });

        // 저장
        Member member = Member.builder()
                .userId(dto.getUserId())
                .userName(dto.getUserName())
                .nickname(dto.getNickname())
                .regDt(LocalDateTime.now())
                .password(dto.getPassword())
                .build();
        memberRepository.save(member);

        return ResponseEntity.ok().body(null);
    }

    @Override
    public ResponseEntity login(loginRequestDto dto) {

        // userName 없음
        Optional<Member> selectedUser = memberRepository.findByUserId(dto.getUserId());

        if (!selectedUser.isPresent()){
            throw new AppException(ErrorCode.USERNAME_NOT_FOUND, "user not exist");
        }

        if (!selectedUser.get().getPassword().equals(dto.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "wrong password");
        }

        return ResponseEntity.ok().body(null);
    }
}
