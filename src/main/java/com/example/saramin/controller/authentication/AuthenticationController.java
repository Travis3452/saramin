package com.example.saramin.controller.authentication;

import com.example.saramin.controllerAdvice.CustomExceptions;
import com.example.saramin.entity.dto.Authentication.LoginForm;
import com.example.saramin.entity.dto.Authentication.ProfileForm;
import com.example.saramin.entity.dto.Authentication.RefreshForm;
import com.example.saramin.entity.dto.Authentication.RegisterForm;
import com.example.saramin.service.RefreshTokenService;
import com.example.saramin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationControllerDocs {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterForm registerForm) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            userService.register(registerForm);
            response.put("status", "200");
            response.put("message", "회원가입 성공");
            return ResponseEntity.ok(response);
        } catch (CustomExceptions.InvalidRegisterCredentialsException e) {
            response.put("status", "400");
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
            response.put("status", "400");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody RefreshForm refreshForm) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            response = refreshTokenService.refresh(refreshForm);

            return ResponseEntity.ok(response);
        } catch (CustomExceptions.InvalidTokenException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PutMapping("/auth/profile")
    public ResponseEntity<Map<String, Object>> profile(Authentication auth, @RequestBody ProfileForm profileForm) {
        Map<String, Object> response = new LinkedHashMap<>();

        try {
            response = userService.profile(auth, profileForm);

            return ResponseEntity.ok(response);
        } catch (CustomExceptions.InvalidProfileCredentialsException e) {
            response.put("400", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
