package com.d201.goodshot.user.service;

import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.dto.UserRequest.JoinRequest;
import com.d201.goodshot.user.exception.DuplicateEmailException;
import com.d201.goodshot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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

}
