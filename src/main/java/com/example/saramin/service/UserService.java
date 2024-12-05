package com.example.saramin.service;

import com.example.saramin.auth.JwtToken;
import com.example.saramin.auth.JwtTokenProvider;
import com.example.saramin.controllerAdvice.CustomExceptions;
import com.example.saramin.entity.UserRole;
import com.example.saramin.entity.dto.Authentication.LoginForm;
import com.example.saramin.entity.dto.Authentication.ProfileForm;
import com.example.saramin.entity.dto.Authentication.RegisterForm;
import com.example.saramin.entity.dto.Authentication.UserDto;
import com.example.saramin.entity.model.LoginHistory;
import com.example.saramin.entity.model.RefreshToken;
import com.example.saramin.entity.model.User;
import com.example.saramin.repository.LoginHistoryRepository;
import com.example.saramin.repository.RefreshTokenRepository;
import com.example.saramin.repository.UserRepository;
import com.example.saramin.util.Base64Encoder;
import com.example.saramin.util.FormValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public void register(RegisterForm registerForm) throws CustomExceptions.InvalidRegisterCredentialsException {
        if(FormValidator.isAnyFieldNull(registerForm)) {
            throw new CustomExceptions.InvalidRegisterCredentialsException("입력하지 않은 값이 존재합니다.");
        } else if (!FormValidator.isValidEmail(registerForm.getEmail())) {
            throw new CustomExceptions.InvalidRegisterCredentialsException("유효하지 않은 이메일 형식입니다.");
        } else if (!FormValidator.isValidPhoneNumber(registerForm.getPhoneNumber())) {
            throw new CustomExceptions.InvalidRegisterCredentialsException("유효하지 않은 전화번호 형식입니다.");
        } else if (userRepository.findByEmail(registerForm.getEmail()).isPresent()) {
            throw new CustomExceptions.InvalidRegisterCredentialsException("이미 가입된 이메일입니다.");
        } else if (!registerForm.getPassword().equals(registerForm.getPasswordCheck())) {
            throw new CustomExceptions.InvalidRegisterCredentialsException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        User user = User.builder()
                .email(registerForm.getEmail())
                .password(Base64Encoder.encode(registerForm.getPassword()))
                .username(registerForm.getUsername())
                .phoneNumber(registerForm.getPhoneNumber())
                .userRole(UserRole.USER)
                .build();

        userRepository.save(user);
    }

    public Map<String, Object> login(LoginForm loginForm) throws CustomExceptions.InvalidLoginCredentialsException {
        String email = loginForm.getEmail();
        String password = loginForm.getPassword();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new CustomExceptions.InvalidLoginCredentialsException("잘못된 이메일 또는 비밀번호입니다. 다시 시도해주세요.")
        );

        if (Base64Encoder.matches(password, user.getPassword())) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

            RefreshToken refreshToken = RefreshToken.builder()
                    .email(email)
                    .value(jwtToken.getRefreshToken())
                    .expireDate(jwtToken.getExpireDate())
                    .build();

            refreshTokenRepository.save(refreshToken);

            LoginHistory loginHistory = LoginHistory.builder()
                    .email(email)
                    .loginDate(new Date(System.currentTimeMillis()))
                    .build();

            loginHistoryRepository.save(loginHistory);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("status", "200");
            response.put("message", "로그인 성공");
            response.put("user", UserDto.toDto(user));
            response.put("jwtToken", jwtToken);

            return response;
        } else {
            throw new CustomExceptions.InvalidLoginCredentialsException("잘못된 이메일 또는 비밀번호입니다. 다시 시도해주세요.");
        }
    }

    public Map<String, Object> profile(Authentication auth, ProfileForm profileForm) throws CustomExceptions.InvalidProfileCredentialsException {
        if (profileForm.getNewPassword() != null && !profileForm.getNewPassword().equals(profileForm.getNewPasswordCheck())) {
            throw new CustomExceptions.InvalidProfileCredentialsException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        } else if (profileForm.getNewPhoneNumber() != null && !FormValidator.isValidPhoneNumber(profileForm.getNewPhoneNumber())) {
            throw new CustomExceptions.InvalidProfileCredentialsException("유효하지 않은 전화번호 형식입니다.");
        }

        String email = auth.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new CustomExceptions.InvalidProfileCredentialsException("존재하지 않는 이메일입니다."));

        if (profileForm.getNewPassword() != null) {
            user.setPassword(Base64Encoder.encode(profileForm.getNewPassword()));
        }

        if (profileForm.getNewPhoneNumber() != null) {
            user.setPhoneNumber(profileForm.getNewPhoneNumber());
        }

        userRepository.save(user);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "200");
        response.put("message", "회원정보 수정 성공");
        response.put("user", user);

        return response;
    }
}
