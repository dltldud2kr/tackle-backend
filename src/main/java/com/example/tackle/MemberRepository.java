package com.example.tackle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);


    @Modifying
    @Query(value = "UPDATE member SET refresh_token = :refreshToken WHERE user_id = :userId", nativeQuery = true)
    void updateRefreshToken(@Param("userId") String userId, @Param("refreshToken") String refreshToken);
}

