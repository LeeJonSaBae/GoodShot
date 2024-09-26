package com.d201.goodshot.user.service;

import com.d201.goodshot.user.repository.EmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;
    private final MessageSource messageSource;

    @Value("${spring.mail.username}")
    private String email;

    // 이메일 인증 번호 전송


    // 이메일 인증 번호 확인


    // 이메일 중복 확인


}
