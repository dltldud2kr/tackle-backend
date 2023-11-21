package com.example.tackle.point.service;

import com.example.tackle.point.entity.Point;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PointService {

    Point create(String memberIdx, Long point, Integer reason);

    List<Point> myPointList(String email);
}
