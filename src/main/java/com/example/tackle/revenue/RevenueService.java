package com.example.tackle.revenue;

import org.springframework.stereotype.Service;

@Service
public interface RevenueService {

    boolean create (Long earningPoint , Integer reason);
}
