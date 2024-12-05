package com.example.saramin.controller.authentication;

import com.example.saramin.controllerAdvice.CustomExceptions;
import com.example.saramin.entity.dto.Authentication.LoginForm;
import com.example.saramin.entity.dto.Authentication.RefreshForm;
import com.example.saramin.entity.dto.Authentication.RegisterForm;
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
                            examples = @ExampleObject(value = "{"
                                    + "\"status\": \"success\", "
                                    + "\"user\": {"
                                    + "\"id\": 1, "
                                    + "\"email\": \"test@gmail.com\", "
                                    + "\"username\": \"홍길동\""
                                    + "}, "
                                    + "\"jwtToken\": {"
                                    + "\"grantType\": \"Bearer\", "
                                    + "\"accessToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGgiOiJVU0VSIiwiZXhwIjoxNzMzMzY3NjAyfQ.LE0l8UYIEDh8revE-XCW1C9AoBbZCNCip9XhhhP7OpY\", "
                                    + "\"refreshToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MzM1Mzg2MDJ9.PCaRodsx8HrXBmDIC_5FqmVUq8c3A3BPOoIVgQ-BVLY\", "
                                    + "\"expireDate\": \"2024-12-07T02:30:02.374+00:00\""
                                    + "}"
                                    + "}"))),
            @ApiResponse(responseCode = "403", description = "로그인 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\": \"error\", \"message\": \"로그인 실패 사유\"}")))
    })
    ResponseEntity<Map<String, Object>> login(@RequestBody LoginForm loginForm) throws CustomExceptions.InvalidLoginCredentialsException;

    @Operation(summary = "로그인", description = "사용자의 인증을 위한 로그인 API")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{"
                                    + "\"status\": \"success\", "
                                    + "\"jwtToken\": {"
                                    + "\"grantType\": \"Bearer\", "
                                    + "\"accessToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImF1dGgiOiJVU0VSIiwiZXhwIjoxNzMzMzY3NzQwfQ.HhNAo5NvGRV3Z65DmjONQwNk8p00NnllYJVsxHvO53c\", "
                                    + "\"refreshToken\": \"eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MzM1Mzg3NDB9.8rcldpvomm2J-sON9sHMThU93dwxZqdW4Z0xT_ObPTI\", "
                                    + "\"expireDate\": \"2024-12-07T02:32:20.239+00:00\""
                                    + "}"
                                    + "}"))),
            @ApiResponse(responseCode = "401", description = "토큰 재발급 실패",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"status\": \"error\", \"message\": \"토큰 재발급 실패 사유\"}")))
    })
    ResponseEntity<Map<String, Object>> refresh(@RequestBody RefreshForm refreshForm) throws CustomExceptions.InvalidLoginCredentialsException;
}
