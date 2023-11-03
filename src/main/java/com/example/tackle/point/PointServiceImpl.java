package com.example.tackle.point;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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
}
