package org.example.restsec.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
@EnableConfigurationProperties(JwtProperties.class)
//@RequiredArgsConstructor
public class JwtTokenProvider {
    //    private final JwtProperties props;
    private final SecretKey secretKey;
    @Getter
    private final Duration accessTokenValidity;

    // 생성자 주입
    public JwtTokenProvider(JwtProperties props) {
//        this.secretKey = props.secret();
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.secret()));
        this.accessTokenValidity = props.accessTokenValidity();
    }

    public String createAccessToken(String subject, String roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenValidity.toMillis());
        return Jwts.builder()
                .subject(subject)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
        // jwt -> 1. 인증 자체 2. 정보
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // sub, claims...
        // sub, roles 등을 get으로 key 호출 가능
    }
}