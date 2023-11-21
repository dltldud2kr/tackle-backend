package com.example.tackle.point;

import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public interface PointService {

    Point create(String memberIdx, Long point, Integer reason);

    List<Point> myPointList(String email);
}
