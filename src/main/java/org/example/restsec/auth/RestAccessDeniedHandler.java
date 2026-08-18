package org.example.restsec.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;

//public class RestAccessDeniedHandler implements AuthenticationEntryPoint {
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        final ObjectMapper objectMapper = new ObjectMapper();
//        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.FORBIDDEN); // 403
        problem.setTitle("Forbidden");
        problem.setDetail("이 작업을 수행할 권한이 없습니다.");
        problem.setInstance(URI.create(request.getRequestURI()));

//        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setContentType("application/problem+json");
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(problem));
    }
}