package com.example.tackle.point.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.point.entity.Point;
import com.example.tackle.point.repository.PointRepository;
import com.example.tackle.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final MemberRepository memberRepository;
    private final PointRepository pointRepository;

    @Override
    public Point create(String memberIdx, Long point, Integer reason) {

        memberRepository.findById(memberIdx)
                .orElseThrow(()-> new  CustomException(CustomExceptionCode.NOT_FOUND_USER));

        Point pointCreate = new Point();
        pointCreate.setIdx(memberIdx);
        pointCreate.setPointChangeAmount(point);
        pointCreate.setPointAccumulationReason(reason);
        pointCreate.setPointUsingDate(LocalDateTime.now());

        pointRepository.save(pointCreate);

        return pointCreate;
    }

    @Override
    public List<Point> myPointList(String email) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND_USER));

        List<Point> pointList =  pointRepository.findByIdx(member.getIdx());

        return pointList;
    }
}
