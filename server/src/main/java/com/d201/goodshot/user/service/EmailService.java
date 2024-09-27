package com.d201.goodshot.user.service;

import com.d201.goodshot.user.dto.Email;
import com.d201.goodshot.user.exception.EmailSendException;
import com.d201.goodshot.user.repository.EmailRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final EmailRepository emailRepository;
    private final MessageSource messageSource;

    @Value("${spring.mail.username}")
    private String email;

    // 이메일 인증 번호 전송
    // random 비밀번호 생성
    private String createRandomCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // 메세지 생성
    public MimeMessage createAuthenticationMessage(String mail, String randomCode) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("GOOD-SHOT 회원가입 인증 메일입니다.", "UTF-8"); // 메일 제목
        message.setText(messageSource.getMessage("spring.mail.authentication", new String[]{randomCode}, Locale.KOREA), "UTF-8", "html"); // 메일 내용
        message.setFrom(new InternetAddress(email, "GoodShot_Official")); //보내는 사람의 메일 주소, 보내는 사람 이름
        return message;
    }

    // 메일 전송
    public void sendAuthenticationEmail(String email) {
        try {
            String randomCode = createRandomCode();
            MimeMessage mimeMessage = createAuthenticationMessage(email, randomCode);
            Email emailCertification = Email.builder()
                    .email(email)
                    .certificationNumber(randomCode)
                    .build();
            emailRepository.save(emailCertification);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new EmailSendException();
        }
    }

    // 이메일 인증 번호 확인


    // 이메일 중복 확인


}
