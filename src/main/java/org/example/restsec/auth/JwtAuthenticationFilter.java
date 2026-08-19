package org.example.restsec.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

// 1회만 실행을 보장하는 필터
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider; // 토큰 해석

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // header로부터 토큰을 추출
        // 'Authorization' 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");

        if (StringUtils.isBlank(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // -> 정제가 덜 끝남 (앞에 토큰을 의미하는 Bearer)
//        String token = authHeader.substring("Bearer ".length());
        String token = authHeader.substring(7); // 7글자를 앞에서 제거
        try {
            Claims claims = jwtTokenProvider.parseClaims(token);
            // .parseSignedClaims(token)
            String username = claims.getSubject(); // 꼭 username은 아니어도 됨 (uuid, seq-id)
            // 이후에 UserDetailService 는 username을 요구하므로 호환 입장에서 username을 사용
            String roles = claims.get("roles", String.class);
            List<GrantedAuthority> authorities = AuthorityUtils
                    .createAuthorityList(roles.split(","));
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
            authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            // 이 두 예외를 제외한 예외는 JWT의 검증 예외가 아님
            SecurityContextHolder.clearContext();
            // -> Spring Security 검증 정보/맥락을 비워줌
        }
        // 이거 안하면 뒤로 안넘어감
        filterChain.doFilter(request, response);
    }
}
