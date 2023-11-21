package com.example.tackle.replies.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Replies {

    @Id
    @GeneratedValue
    private Long repliesId;

    private String idx; // 카카오 고유번호 12자리
    private String nickname;
    private Long postId;

    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdMinutesAgo;
//    private LocalDateTime updatedAt; // 수정 불가라니까 제외

}
