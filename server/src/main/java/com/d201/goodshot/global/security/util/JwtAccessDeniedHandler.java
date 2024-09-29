package com.d201.goodshot.global.security.util;

import com.d201.goodshot.global.security.exception.SecurityExceptionList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.d201.goodshot.global.security.exception.SecurityExceptionList.ACCESS_DENIED_03;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private void setResponse(HttpServletResponse response, SecurityExceptionList exception) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("code", exception.getCode());
        jsonResponse.put("message", exception.getMessage());

        response.getWriter().print(jsonResponse);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 필요한 권한이 없이 접근하려 할때 403
        setResponse(response, ACCESS_DENIED_03);
    }
}
