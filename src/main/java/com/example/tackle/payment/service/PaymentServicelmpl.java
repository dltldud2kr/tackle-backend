package com.example.tackle.payment.service;

import com.example.tackle._enum.CustomExceptionCode;
import com.example.tackle.exception.CustomException;
import com.example.tackle.member.entity.Member;
import com.example.tackle.member.repository.MemberRepository;
import com.example.tackle.payment.dto.PaymentDto;
import com.example.tackle.payment.entity.Payment;
import com.example.tackle.payment.repository.PaymentRepository;
import com.example.tackle.replies.dto.RepliesDto;
import com.example.tackle.replies.entity.Replies;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;

@Service
public class PaymentServicelmpl implements PaymentService {

    private final String TOKEN_URL = "https://api.iamport.kr/users/getToken";
    private final String VALIDATE_URL = "https://api.iamport.kr/payments/{imp_uid}";

    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    public PaymentServicelmpl(MemberRepository memberRepository, PaymentRepository paymentRepository) {
        this.memberRepository = memberRepository;
        this.paymentRepository = paymentRepository;
    }

    public JsonObject getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("imp_key", "6267821178243100"); // imp_restAPI KEY임
        requestBody.addProperty("imp_secret", "Jy23lgFvZqoa0ZXHOxFdpE0sdvVz5zAnL70y3oWb3b7vyKhb3583SKJy1dpzVZJu7BZMt4xG5YmnAIcF"); // imp_RESTAPI_시크릿키

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(TOKEN_URL, request, String.class);

        Gson gson = new Gson();
        JsonObject response = gson.fromJson(responseEntity.getBody(), JsonObject.class);
        JsonObject responseResult = response.getAsJsonObject("response");

        return responseResult;
    }

    /* 절차 :
    *   포트원 API 액세스 토큰 발급 (유효기간 : 30분) ->
    *   발급받은 액세스 토큰으로 들어온 요청의 결제 고유번호 대조 -> 유효성 검사
    *   결과값 DB 저장 및 결과값 JSON member_idx 추가하여 반환
    * */

    public JsonObject validatePayment(String imp_uid, String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity =
                    restTemplate.exchange(VALIDATE_URL, HttpMethod.GET, entity, String.class, imp_uid);

            Gson gson = new Gson();
            JsonObject response = gson.fromJson(responseEntity.getBody(), JsonObject.class);

            return response;
        } catch (HttpClientErrorException e) {
            // 실패 시 예외 처리
            JsonObject failResponse = new JsonObject();
            failResponse.addProperty("status", "fail");
            return failResponse;
        }
    }


    @Override
    public boolean create(PaymentDto dto, String mem_idx) {
        Member member = memberRepository.findById(mem_idx)
                .orElseThrow(() -> new CustomException(CustomExceptionCode.NOT_FOUND));

            // imp_uid 중복 체크
            Payment existingPayment = paymentRepository.findByImpUid(dto.getImpUid());
            if (existingPayment != null) {
                // 중복된 imp_uid 값을 가진 결제가 이미 존재하면 오류를 반환
                throw new CustomException(CustomExceptionCode.DUPLICATE_IMP_UID);
            }

        Payment payment = Payment.builder()
                .idx(dto.getIdx())
                .payment_amount(dto.getPayment_amount())
                .impUid(dto.getImpUid())
                .payment_date(dto.getPayment_date())
                .build();

        paymentRepository.save(payment);

        return true;
    }
}

