package com.example.tackle.member.service;

import com.example.tackle.member.dto.MemberDto;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.auth.JwtTokenProvider;
import com.example.tackle.dto.TokenDto;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;


    /**
     * 1. 로그인 요청으로 들어온 ID, PWD 기반으로 Authentication 객체 생성
     * 2. authenticate() 메서드를 통해 요청된 Member 에 대한 검증이 진행 => loadUserByUsername 메서드를 실행.
     * 해당 메서드는 검증을 위한 유저 객체를 가져오는 부분으로써, 어떤 객체를 검증할 것인지에 대해 직접 구현
     * 3. 검증이 정상적으로 통과되었다면 인증된 Authentication 객체를 기반으로 JWT 토큰을 생성
     * @author dltldud2kr
     */
    @Transactional
    public TokenDto login(String email, String memberIdx) {
        Optional<Member> optionalMember =  memberRepository.findById(memberIdx);
        String email1 = optionalMember.get().getEmail();

        memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_EMAIL));

        //사용자 인증 정보를 담은 토큰을 생성함. (이메일, 멤버 인덱스 정보 포함 )
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, memberIdx);

        //authenticationManagerBuilder를 사용하여 authenticationToken을 이용한 사용자의 인증을 시도합니다.
        // 여기서 실제로 로그인 발생  ( 성공: Authentication 반환 //   실패 : Exception 발생
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증이 된 경우 JWT 토큰을 발급  (요청에 대한 인증처리)
        TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

        if (tokenDto.getAccessToken().isEmpty()){
            log.info(tokenDto.getAccessToken());
        }

        return tokenDto;
    }



    public TokenDto createToken(String memberIdx) {
        Member member = memberRepository.findById(memberIdx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

        if (jwtTokenProvider.validateToken(member.getRefreshToken())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());
            System.out.println("authenticationToken : "+authenticationToken);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            return tokenDto;
        } else {
            //만료된 리프레쉬 토큰.
            throw new CustomException(CustomExceptionCode.EXPIRED_JWT);
        }
    }

    @Override
    public String getReturnAccessToken(String code, HttpServletRequest request) {
        String access_token = "";
        String refresh_token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //HttpURLConnection 설정 값 셋팅
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // buffer 스트림 객체 값 셋팅 후 요청
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
//           sb.append("&client_id=b22a0873d0ccefbc5f331106fa7b9287");  // REST API 키

            String origin = request.getHeader("Origin"); //요청이 들어온 Origin을 가져옵니다.
            sb.append("&client_id=ccf25614050bf5afb0bf4c82541cebb8");  // REST API 키

            sb.append("&redirect_uri=http://localhost:8080/auth/kakao/callback");
            // 테스트 서버, 퍼블리싱 서버 구분
            /*
            if("http://localhost:8080".equals(origin)){

                sb.append("&redirect_uri=http://localhost:8080/auth/kakao/callback"); // 앱 CALLBACK 경로
            } else {
                sb.append("&redirect_uri=https://app.lunaweb.dev/auth/kakao/callback"); // 다른 경로
            }

             */
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //  RETURN 값 result 변수에 저장
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String br_line = "";
            String result = "";

            while ((br_line = br.readLine()) != null) {
                result += br_line;
            }

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);


            // 토큰 값 저장 및 리턴
            access_token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token: " + access_token);
            System.out.println("refresh_token: " + refresh_token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_token;
    }

    @Override
    public Map<String, Object> getUserInfo(String access_token) {
        Map<String,Object> resultMap =new HashMap<>();
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            //요청에 필요한 Header에 포함될 내용
            conn.setRequestProperty("Authorization", "Bearer " + access_token);

            int responseCode = conn.getResponseCode();
            log.info("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String br_line = "";
            String result = "";

            while ((br_line = br.readLine()) != null) {
                result += br_line;
            }
            log.info("response:" + result);


            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);
            log.warn("element:: " + element);
            JsonObject properties = element.getAsJsonObject().get("properties").getAsJsonObject();
            JsonObject kakao_account = element.getAsJsonObject().get("kakao_account").getAsJsonObject();
            log.warn("id:: "+element.getAsJsonObject().get("id").getAsString());
            String id = element.getAsJsonObject().get("id").getAsString();
            String nickname = properties.getAsJsonObject().get("nickname").getAsString();
            String email = kakao_account.getAsJsonObject().get("email").getAsString();
            // 프로필 이미지 정보 반환
            String profileImage = properties.getAsJsonObject().get("profile_image").getAsString();

            log.warn("email:: " + email);
            resultMap.put("nickname", nickname);
            resultMap.put("id", id);
            resultMap.put("email", email);
            // Map에 프로필 이미지 정보를 추가합니다.
            resultMap.put("profile_image", profileImage);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultMap;
    }

    @Transactional
    @Override
    public TokenDto join(String email, String memberIdx, String nickname) {
        try {
            //해당 이메일이 존재하는지 확인.
            Optional<Member> optionalMember =  memberRepository.findById(email);
            if(optionalMember.isPresent()) {
                throw new CustomException(CustomExceptionCode.DUPLICATED);
            }
            //해당 이메일이 디비에 존재하는지 확인.
            Member member = Member.builder()
                    .email(email)
                    .idx(memberIdx)
                    .point(50000000L)
                    .refreshToken(null)
                    .role(0)
                    .nickname(nickname)
                    .regDt(LocalDateTime.now())
                    .build();
            memberRepository.save(member);

            //사용자 인증 정보를 담은 토큰을 생성함. (이메일, 멤버 인덱스 정보 포함 )
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, memberIdx);

            //authenticationManagerBuilder를 사용하여 authenticationToken을 이용한 사용자의 인증을 시도합니다.
            // 여기서 실제로 로그인 발생  ( 성공: Authentication 반환 //   실패 : Exception 발생
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            // 인증이 된 경우 JWT 토큰을 발급  (요청에 대한 인증처리)
            TokenDto tokenDto = jwtTokenProvider.generateToken(authentication);
            log.info(tokenDto.getAccessToken());
            if (tokenDto.getAccessToken().isEmpty()){
                log.info(tokenDto.getAccessToken());
                log.info("token empty");
            }
            return tokenDto;



        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //이메일 -> 사용자 정보를 찾아고  pk
    public Member getMember(String email) {
        Optional<Member> byEmail = memberRepository.findByEmail(email);
        // 비어있는 경우 예외 처리 또는 기본값을 반환하는 로직 추가

        return byEmail.orElse(null);
    }


    // 관리자 회원 리스트
    public List<Member> getMemberList(String access_token) {
        if (!access_token.isEmpty()) {
            Member member = memberRepository.findByEmail(access_token)
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

            if (member.getRole() == 1) {
                return memberRepository.findAll();
            } else {
                throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
            }
        } else {
            throw new CustomException(CustomExceptionCode.UNAUTHORIZED_USER);
        }
    }

    public MemberDto getMemberInfo(String access_token) {
        if (!access_token.isEmpty()) {
            Member member = memberRepository.findByEmail(access_token)
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

            // Entity를 Dto로 변환
            MemberDto memberDto = new MemberDto();
            memberDto.setPoint(member.getPoint());
            memberDto.setNickname(member.getNickname());
            memberDto.setRegAt(member.getRegDt());
            return memberDto;
        } else {
            throw new CustomException(CustomExceptionCode.NOT_FOUND);
        }
    }

    @Override
    public boolean update(String idx, Member dto) {
        Member member = memberRepository.findById(idx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.UNAUTHORIZED_USER));

        if (member.getIdx().equals(idx) && !memberRepository.existsByNickname(dto.getNickname())) {
            member.setNickname(dto.getNickname());              // existsByNickname 해당 닉네임의 존재 여부 확인
            memberRepository.save(member);
            return true;
        }

        throw new CustomException(CustomExceptionCode.DUPLICATED);
    }



}
