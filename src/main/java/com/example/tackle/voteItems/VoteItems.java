package com.example.tackle.voteItems;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class VoteItems {

    @Id
    @GeneratedValue
    private Long itemId;

    private Long postId;
    private String content;
    private Long voteCount;
}
