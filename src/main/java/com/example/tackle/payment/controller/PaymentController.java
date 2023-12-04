package com.example.tackle.payment.controller;

import com.example.tackle._enum.ApiResponseCode;
import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.dto.ResultDTO;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.member.service.MemberService;
import com.example.tackle.member.service.MemberServiceImpl;
import com.example.tackle.payment.dto.PaymentDto;
import com.example.tackle.payment.entity.Payment;
import com.example.tackle.payment.repository.PaymentRepository;
import com.example.tackle.payment.service.PaymentService;
import com.example.tackle.payment.service.PaymentServicelmpl;
import com.example.tackle.point.entity.Point;
import com.example.tackle.point.repository.PointRepository;
import com.example.tackle.point.service.PointService;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/v1")

public class PaymentController {

    private final PaymentServicelmpl paymentServicelmpl;
    private final MemberRepository memberRepository;
    private final MemberServiceImpl memberService;
    private final PointService pointService;
    private final PointRepository pointRepository;

    public PaymentController(PaymentServicelmpl paymentServicelmpl, MemberRepository memberRepository, MemberServiceImpl memberService, PointService pointService, PointRepository pointRepository) {
        this.paymentServicelmpl = paymentServicelmpl;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
        this.pointService = pointService;
        this.pointRepository = pointRepository;
    }


    @Operation(summary = "페이 충전 처리", description = "" +
            "프론트앤드에서 처리 후 백앤드에서 유효성 검사 및 DB에 값 증감 및 추가" +
            "\n### HTTP STATUS 에 따른 조회 결과" +
            "\n- 200: 서버요청 정상 성공 "+
            "\n- 500: 서버에서 요청 처리중 문제가 발생" +
            "\n### Result Code 에 따른 요청 결과" +
            "\n-토큰 값이 올바르지 않습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 투표 성공"),
    })
    
    @PostMapping("/payment/valid/{imp_uid}")
    @Transactional
    public ResultDTO validatePayment(@PathVariable String imp_uid, Principal principal) {
        String email = "";
        if (principal == null) {
            // 사용자가 로그인하지 않은 경우에 대한 처리
            email = "";
            return null;
        } else {
            email = principal.getName(); // 사용자가 로그인한 경우 이메일 가져오기
        }
        try {
            JsonObject tokenResponse = paymentServicelmpl.getAccessToken();
            String accessToken = tokenResponse.get("access_token").getAsString();

            JsonObject validateResponse = paymentServicelmpl.validatePayment(imp_uid, accessToken);

            Map<String, Object> response = new Gson().fromJson(validateResponse, new TypeToken<Map<String, Object>>() {
            }.getType());

            int code = ((Double) response.get("code")).intValue();

            // JsonObject를 Map으로 변환
            Gson gson = new Gson();
            String jsonString = gson.toJson(response.get("response"));
            JsonObject responseObject = gson.fromJson(jsonString, JsonObject.class);
            Long amount = responseObject.get("amount").getAsLong();
            String status = responseObject.get("status").getAsString();

            // status == "paid" 이면 "결제 성공"
            response.put("code", code);
            ((Map<String, Object>) response.get("response")).put("amount", amount);

            // JWT Token으로 멤버 idx 매핑
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new CustomException(CustomExceptionCode.PAYMENT_EXPIRED_JWT));
            String mem_idx = member.getIdx();
            response.put("memberIdx", mem_idx); // 회원의 idx 값을 추가

            if (status.equals("paid")) {  // 유효성검사에서 "충전 성공" 이면
                // Payment 테이블에 충전내역 저장 -- 같은 고유번호로 충전 중복 방지
                PaymentDto paymentDto = PaymentDto.builder()
                        .idx(mem_idx)
                        .payment_amount(amount)
                        .impUid(imp_uid)
                        .payment_date(LocalDateTime.now())
                        .build();
                paymentServicelmpl.create(paymentDto, mem_idx);
                // 예외 처리 필요

                // member 테이블에 point 증감
                member.increasePoint(amount);
                memberRepository.save(member);


                // 포인트 충전내역 저장
                Point pointCreate = pointService.create(mem_idx, amount, 4);
                pointRepository.save(pointCreate);

            } else {
                throw new CustomException(CustomExceptionCode.CHARGING_FAILED);
            }
            return ResultDTO.of(true, ApiResponseCode.SUCCESS.getCode(), "충전 성공",response);
        } catch (CustomException e) {
            return ResultDTO.of(false,  e.getCustomErrorCode().getStatusCode(), e.getDetailMessage(), null);

        }
    }
}
