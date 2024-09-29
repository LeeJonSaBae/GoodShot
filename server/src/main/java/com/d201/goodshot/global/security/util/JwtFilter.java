package com.d201.goodshot.global.security.util;

import com.d201.goodshot.user.domain.User;
import com.d201.goodshot.user.exception.NotFoundUserException;
import com.d201.goodshot.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenUtil tokenUtil;
    private final UserRepository userRepository;

    // Request Header 에서 토큰 정보를 꺼내오기
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();

        // Swagger 경로에 대한 JWT 필터 우회 처리
        if (uri.startsWith("/v3/api-docs") || uri.startsWith("/swagger-ui") || uri.startsWith("/swagger-resources")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. Request Header 에서 토큰을 꺼냄
        String token = resolveToken(request);
        log.info("JWT Filter Access token: {}", token);

        // 2. validateToken 으로 토큰 유효성 검사
        // 정상 토큰이면 해당 토큰으로 Authentication 을 가져와서 SecurityContext 에 저장
        // 만약 validateToken() 과정에서 예외 발생시 Authentication 이 만들어지지 않아 AuthenticationEntryPoint 호출됨
        if (StringUtils.hasText(token) && tokenUtil.validateToken(token, request)) {
            String userEmail = tokenUtil.getSubject(token);
            User user = userRepository.findByEmail(userEmail).orElseThrow(NotFoundUserException::new);
            tokenUtil.makeAuthentication(user);
        }

        filterChain.doFilter(request, response);
    }

}
