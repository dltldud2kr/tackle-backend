package com.example.tackle.member.repository;

import com.example.tackle.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByEmail(String email);


    @Modifying
    @Query(value = "UPDATE member SET refresh_token = :refreshToken WHERE email = :email", nativeQuery = true)
    void updateRefreshToken(@Param("email") String email, @Param("refreshToken") String refreshToken);

    Optional<Member> findByIdx(String idx);
}

