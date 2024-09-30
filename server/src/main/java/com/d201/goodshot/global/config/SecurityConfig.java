package com.d201.goodshot.global.config;

import com.d201.goodshot.global.security.util.JwtAccessDeniedHandler;
import com.d201.goodshot.global.security.util.JwtAuthenticationEntryPoint;
import com.d201.goodshot.global.security.util.JwtFilter;
import com.d201.goodshot.global.security.util.TokenUtil;
import com.d201.goodshot.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(auth -> auth.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(new JwtFilter(tokenUtil, userRepository), UsernamePasswordAuthenticationFilter.class) // jwt filter 등록
                .authorizeHttpRequests((a) -> {
                    a.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                            .requestMatchers(
                                    "/v3/api-docs/**",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/swagger-resources/**",
                                    "/webjars/**"
                            ).permitAll()
                            .requestMatchers("/users/join/**").permitAll()
                            .requestMatchers("/users/login").permitAll()
                            .requestMatchers("/users/logout").permitAll()
                            .requestMatchers("/users/reissue").permitAll()
                            .requestMatchers("/users/email").permitAll()
                            .requestMatchers("/users/check-email").permitAll()
                            .requestMatchers("/users/temporary-password").permitAll()
                            .requestMatchers("/experts").permitAll()
                            .requestMatchers("/experts/**").permitAll()
                            .anyRequest().authenticated();
                });

        return http.build();
    }

}
