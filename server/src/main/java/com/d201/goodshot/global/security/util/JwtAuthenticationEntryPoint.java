package com.d201.goodshot.global.security.util;

import com.d201.goodshot.global.security.exception.SecurityExceptionList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.d201.goodshot.global.security.exception.SecurityExceptionList.*;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private void setResponse(HttpServletResponse response, SecurityExceptionList exceptionCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        JSONObject responseJson = new JSONObject();
        responseJson.put("timestamp", LocalDateTime.now().withNano(0).toString());
        responseJson.put("message", exceptionCode.getMessage());
        responseJson.put("errorCode", exceptionCode.getCode());

        response.getWriter().print(responseJson);
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String exception = String.valueOf(request.getAttribute("exception"));
        if (exception.equals(MALFORMED_TOKEN.getMessage()))
            setResponse(response, MALFORMED_TOKEN);

        else if (exception.equals(ILLEGAL_TOKEN.getMessage()))
            setResponse(response, ILLEGAL_TOKEN);

        else if (exception.equals(EXPIRED_TOKEN.getMessage()))
            setResponse(response, EXPIRED_TOKEN);

        else if (exception.equals(UNSUPPORTED_TOKEN.getMessage()))
            setResponse(response, UNSUPPORTED_TOKEN);

        else setResponse(response, ACCESS_DENIED);
    }

}
