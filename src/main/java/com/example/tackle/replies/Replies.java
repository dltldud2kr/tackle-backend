package com.example.tackle.replies;


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

    private Long idx;
    private Long postId;

    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
