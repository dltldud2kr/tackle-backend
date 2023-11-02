package com.example.tackle.revenue;

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
public class Revenue {

    @Id
    @GeneratedValue
    private Long revenueId;
    private Long earningPoint;
}
