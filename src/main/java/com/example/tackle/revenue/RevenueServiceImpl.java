package com.example.tackle.revenue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class RevenueServiceImpl implements RevenueService {

    private final RevenueRepository revenueRepository;
    @Override
    public boolean create(Long earningPoint, Integer reason) {

        Revenue revenue = Revenue.builder()
                .earningPoint(earningPoint)
                .earningReason(reason)
                .created_at(LocalDateTime.now())
                .build();

        revenueRepository.save(revenue);

        return false;
    }
}
