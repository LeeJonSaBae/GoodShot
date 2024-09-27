package com.d201.goodshot.global.security.util;

import com.d201.goodshot.global.security.dto.CustomUser;
import com.d201.goodshot.global.security.dto.Token;
import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.dto.Auth;
import com.d201.goodshot.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.d201.goodshot.global.security.exception.SecurityExceptionList.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class TokenUtil {

    @Value("${security.secret-key}")
    private String secret;
    private final Long accessTokenExpireTime = 60 * 60L; // 1시간
    private final Long refreshTokenExpireTime = 60 * 60 * 24 * 7L;
    private SecretKey secretKey;
    private final RefreshTokenRepository refreshTokenRepository;

    // secretKey 초기화
    @PostConstruct // 의존성 주입이 완료된 후에 초기화 진행
    public void generateKey() {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // 토큰 검증
    public String getSubject(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Object getClaim(String token, String claimName, Class<?> classType) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get(claimName, classType);
    }

    // 토큰 생성
    public Token generateToken(User user) {
        String accessToken = Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .claim("email", user.getEmail())
                .expiration(Date
                        .from(Instant.now()
                        .plus(accessTokenExpireTime, ChronoUnit.SECONDS)))
                .signWith(secretKey).compact();

        String refreshToken = Jwts.builder().
                subject(user.getEmail())
                .issuedAt(Timestamp.valueOf(LocalDateTime.now()))
                .expiration(Date
                        .from(Instant.now().plus(refreshTokenExpireTime, ChronoUnit.SECONDS)))
                .signWith(secretKey).compact();

        // redis 에 refresh Token 저장
        refreshTokenRepository.save(Auth.builder().email(user.getEmail()).refreshToken(refreshToken).build());

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(CustomUser user) {
        List<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(user, "",
                grantedAuthorities);
    }

    public void makeAuthentication(User user) {
        // Authentication 정보 만들기
        CustomUser customUser = CustomUser.builder()
                .email(user.getEmail())
                .roles(Arrays.asList(user.getRole().toString()))
                .build();

        // ContextHolder 에 Authentication 정보 저장
        Authentication auth = getAuthentication(customUser);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    public boolean validateToken(String token, HttpServletRequest request) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            request.setAttribute("exception", MALFORMED_TOKEN.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
            request.setAttribute("exception", EXPIRED_TOKEN.getMessage());
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
            request.setAttribute("exception", UNSUPPORTED_TOKEN.getMessage());
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            request.setAttribute("exception", ILLEGAL_TOKEN.getMessage());
        } catch (Exception e) {
            log.info(e.getMessage());
            request.setAttribute("exception", ACCESS_DENIED.getMessage());
        }
        return false;
    }

}
