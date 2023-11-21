package com.example.tackle.replies.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RepliesDto {
    private Long repliesId;

    private String idx; // 카카오 고유번호 12자리

    private String nickname;
    private Long postId;

    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdMinutesAgo;
}
