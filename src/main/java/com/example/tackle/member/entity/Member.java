package com.example.tackle.member.entity;

import com.example.tackle._enum.MemberRoles;
import com.example.tackle.member.service.CustomUserDetailsService;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Member implements UserDetails {
    @Id
    private String idx;


    private String email;
    private String refreshToken; //리프레쉬 토큰
    private String userName;
    private String nickname;
    private Long point;
    @Enumerated(EnumType.STRING)
    private MemberRoles role;
    private LocalDateTime regDt;
    private LocalDateTime udtDt;


    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    //authentication.getName()이 이것을 반환함.
    @Override
    public String getUsername() {
        return email;
    }

    public String getIdx() {return idx;}


    // idx 카카오 고유번호  (pk값) 을 password 로 둠.
    // password로 둔 이유는 스프링 시큐리티 UserDetails객체를 사용하기 때문
    // 이 객체는 userName과 password를 받아야함.
    // 우리 프로젝트는 email과 카카오 고유 idx 값만 확인해서 로그인 시키기 때문에 password 대신 idx 를 넣음
    @Override
    public String getPassword() {
        return idx;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}