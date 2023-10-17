package com.example.tackle;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.auth.JwtTokenProvider;
import com.example.tackle.dto.JoinRequestDto;
import com.example.tackle.dto.TokenDto;
import com.example.tackle.dto.loginRequestDto;
import com.example.tackle.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{
    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 1. 로그인 요청으로 들어온 ID, PWD 기반으로 Authentication 객체 생성
     * 2. authenticate() 메서드를 통해 요청된 Member에 대한 검증이 진행 => loadUserByUsername 메서드를 실행. 해당 메서드는 검증을 위한 유저 객체를 가져오는 부분으로써, 어떤 객체를 검증할 것인지에 대해 직접 구현
     * 3. 검증이 정상적으로 통과되었다면 인증된 Authentication객체를 기반으로 JWT 토큰을 생성
     */
    @Transactional
    public TokenDto login(String userId, String password) {
        log.info("findByUser before");
        memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_EMAIL));
        log.info("UsernamePasswordAuthenticationToken before");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId, password);

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("authentication after");

        //이게 문제.
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
        log.info(tokenDto.getAccessToken());
        if (tokenDto.getAccessToken().isEmpty()){
            log.info(tokenDto.getAccessToken());
            log.info("token empty");
        }
        log.info("generateToken after");
        return tokenDto;
    }

    public TokenDto createToken(Long memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        if (jwtTokenProvider.validateToken(member.getRefreshToken())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getUserId(), member.getPassword());
            System.out.println("authenticationToken : "+authenticationToken);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            return tokenDto;
        } else {
            //만료된 리프레쉬 토큰.
            throw new CustomException(CustomExceptionCode.EXPIRED_JWT);
        }
    }

    @Transactional
    public boolean join(JoinRequestDto request) {
        try {
            //해당 이메일이 존재하는지 확인.
            if(this.getMember(request.getUserId()) != null) {
                throw new CustomException(CustomExceptionCode.DUPLICATED);
            }
            //해당 이메일이 디비에 존재하는지 확인.
            Member member = Member.builder()
                    .userId(request.getUserId())
                    .userName(request.getUserName())
                    .nickname(request.getNickname())
                    .refreshToken(null)
                    .regDt(LocalDateTime.now())
                    .password(request.getPassword())
                    .build();
            memberRepository.save(member);
            return true;
        } catch (DataAccessException e) {
            System.err.println("DataAccessException : " + e);
            return false;
        }
    }

    //이메일 -> 사용자 정보를 찾아고  pk
    public Member getMember(String userId) {
        Optional<Member> byEmail = memberRepository.findByUserId(userId);
        // 비어있는 경우 예외 처리 또는 기본값을 반환하는 로직 추가

        return byEmail.orElse(null);
    }

}
