package com.example.tackle;

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
public class Member {
    @Id
    @GeneratedValue
    private Long idx;


    private String userId;
    private String userName;
    private String nickname;
    private String password;
    private LocalDateTime regDt;
    private LocalDateTime udtDt;

}