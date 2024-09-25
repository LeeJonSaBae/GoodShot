package com.d201.goodshot.user.service;

import com.d201.goodshot.global.security.dto.Token;
import com.d201.goodshot.global.security.exception.InvalidTokenException;
import com.d201.goodshot.global.security.util.TokenUtil;
import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.dto.Auth;
import com.d201.goodshot.user.dto.UserRequest.JoinRequest;
import com.d201.goodshot.user.dto.UserRequest.LoginRequest;
import com.d201.goodshot.user.exception.*;
import com.d201.goodshot.user.repository.RefreshTokenRepository;
import com.d201.goodshot.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenUtil tokenUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // 회원가입
    public void join(JoinRequest joinRequest) {

        // 이메일 중복 확인
        if(userRepository.existsByEmail(joinRequest.getEmail())) {
            throw new DuplicateEmailException();
        }

        // 비밀번호 encode
        String password = passwordEncoder.encode(joinRequest.getPassword());

        User user = User.builder()
                .email(joinRequest.getEmail())
                .password(password)
                .name(joinRequest.getName())
                .build();

        userRepository.save(user);
    }

    // 회원탈퇴
    public void exit(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);

        // refresh token
        Optional<Auth> refreshToken = refreshTokenRepository.findById(email);

        // 이미 로그아웃된 상태면 예외 던지기
        if (refreshToken.isEmpty()) {
            throw new AlreadyLogoutException();
        }

        refreshToken.ifPresent(refreshTokenRepository::delete);
        userRepository.delete(user);

    }

    // 로그인
    public Token login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(LoginFailException::new);
        if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return tokenUtil.generateToken(user);
        } else {
            throw new LoginFailException();
        }
    }

    // 로그아웃
    public void logout(String email) {
        // refresh token delete
        Optional<Auth> refreshToken = refreshTokenRepository.findById(email); // refresh token 찾기

        // 이미 로그아웃된 상태면 예외 던지기
        if (refreshToken.isEmpty()) {
            throw new AlreadyLogoutException();
        }

        refreshToken.ifPresent(refreshTokenRepository::delete); // refreshTokenRepository.delete(refreshToken)
        // ifPresent : 객체 값이 존재하면 해당 람다식 실행
    }

    // 토큰 재발급
    public Token reissue(String refreshToken, HttpServletRequest request) {
        if (tokenUtil.validateToken(refreshToken, request)) { // refresh token 유효한지 확인
            try {
                String email = tokenUtil.getSubject(refreshToken); // email 찾기
                // token 검증
                Auth auth = refreshTokenRepository.findById(email).orElseThrow(InvalidTokenException::new);

                log.info("input refreshToken: {}", refreshToken);
                log.info("redis refreshToken: {}", auth.getRefreshToken());

                // 현재 redis 에 있는 token, 사용자가 가지고 있는 token 동일한지 확인
                if (StringUtils.equals(auth.getRefreshToken(), refreshToken)) {
                    // 새로운 token 발급
                    return tokenUtil.generateToken(User.builder().email(email).build());
                }
            } catch (Exception e) {
                throw new InvalidTokenException();
            }
        }
        throw new InvalidTokenException();
    }

    // 비밀번호 변경
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(NotFoundUserException::new);
        if(!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialException();
        }
        user.changePassword(passwordEncoder.encode(newPassword));
    }

}
