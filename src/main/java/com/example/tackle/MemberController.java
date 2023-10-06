package com.example.tackle;

import com.example.tackle.dto.loginRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class MemberController {
    private final MemberService memberService;



    @Operation(summary = "회원 가입 요청", description = "회원 가입", tags = {"MemberController"})

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(schema = @Schema(implementation = MemberController.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @PostMapping("/join")
    public ResponseEntity join(
            @Parameter(description = "회원 ID", required = true, example = "1")
            @RequestBody MemberJoinRequest dto) {

        return ResponseEntity.ok(memberService.join(dto));
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody loginRequestDto parameter) {


        return ResponseEntity.ok(memberService.login(parameter));

    }



}
