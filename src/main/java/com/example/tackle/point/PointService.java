package com.example.tackle.point;

import org.springframework.stereotype.Service;

@Service
public interface PointService {

    Point create(String memberIdx, Long point, Integer reason);
}
