package com.example.tackle.payment;

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
public class Payment {

    @Id
    @GeneratedValue
    private Long paymentId;
    private Long idx;

//    private enum paymentMethod;
    private Long paymentAmount;
    private LocalDateTime paymentDate;
}
