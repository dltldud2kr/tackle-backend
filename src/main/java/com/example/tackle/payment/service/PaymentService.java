package com.example.tackle.payment.service;

import com.example.tackle.payment.dto.PaymentDto;
import com.example.tackle.replies.dto.RepliesDto;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    boolean create(PaymentDto dto, String mem_idx);
}
