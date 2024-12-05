package com.example.saramin.controller.authentication;

import com.example.saramin.controllerAdvice.CustomExceptions;
import com.example.saramin.entity.dto.LoginForm;
import com.example.saramin.entity.dto.RegisterForm;
import com.example.saramin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationControllerDocs {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterForm registerForm) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            userService.register(registerForm);
            response.put("status", "success");
            response.put("message", "회원가입 성공");
            return ResponseEntity.ok(response);
        } catch (CustomExceptions.InvalidRegisterCredentialsException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginForm loginForm) {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            response = userService.login(loginForm);

            return ResponseEntity.ok(response);
        } catch (CustomExceptions.InvalidLoginCredentialsException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
