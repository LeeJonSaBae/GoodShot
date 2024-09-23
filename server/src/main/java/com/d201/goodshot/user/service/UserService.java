package com.d201.goodshot.user.service;

import com.d201.goodshot.global.security.dto.Token;
import com.d201.goodshot.global.security.util.TokenUtil;
import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.dto.RefreshToken;
import com.d201.goodshot.user.dto.UserRequest.JoinRequest;
import com.d201.goodshot.user.dto.UserRequest.LoginRequest;
import com.d201.goodshot.user.exception.AlreadyLogoutException;
import com.d201.goodshot.user.exception.DuplicateEmailException;
import com.d201.goodshot.user.exception.LoginFailException;
import com.d201.goodshot.user.repository.RefreshTokenRepository;
import com.d201.goodshot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
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
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findById(email); // refresh token 찾기

        // 이미 로그아웃된 상태면 예외 던지기
        if (refreshToken.isEmpty()) {
            throw new AlreadyLogoutException();
        }

        refreshToken.ifPresent(refreshTokenRepository::delete); // refreshTokenRepository.delete(refreshToken)
        // ifPresent : 객체 값이 존재하면 해당 람다식 실행
    }

}
