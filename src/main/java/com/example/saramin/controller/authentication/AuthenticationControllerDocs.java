package com.example.saramin.controller.authentication;

import com.example.saramin.controllerAdvice.CustomExceptions;
import com.example.saramin.entity.dto.LoginForm;
import com.example.saramin.entity.dto.RegisterForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface AuthenticationControllerDocs {

    @Operation(summary = "회원가입", description = "신규 사용자의 회원가입을 위한 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\": \"success\", \"message\": \"회원가입 성공\"}"))),
            @ApiResponse(responseCode = "400", description = "회원가입 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\": \"error\", \"message\": \"회원가입 실패 사유\"}")))
    })
    ResponseEntity<Map<String, Object>> register(@RequestBody RegisterForm registerForm) throws CustomExceptions.InvalidRegisterCredentialsException;

    @Operation(summary = "로그인", description = "사용자의 인증을 위한 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\": \"success\", \"message\": \"회원가입 성공\"}"))),
            @ApiResponse(responseCode = "403", description = "로그인 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\": \"error\", \"message\": \"로그인 실패 사유\"}")))
    })
    ResponseEntity<Map<String, Object>> login(@RequestBody LoginForm loginForm) throws CustomExceptions.InvalidLoginCredentialsException;
}
