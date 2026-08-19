package org.example.restsec.controller;

import lombok.RequiredArgsConstructor;
import org.example.restsec.auth.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(
            AuthenticationConfiguration authenticationConfiguration,
            JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationConfiguration
                .getAuthenticationManager();
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public AccessTokenDTO login(
            @RequestBody LoginDTO dto // username, password
    ) {
        // import org.springframework.security.core.Authentication;
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.username(),
                        dto.password()
                )
        );
        String roles = "ROLE_USER";
        String accessToken = jwtTokenProvider.createAccessToken(
                auth.getName(), roles);
        return new AccessTokenDTO(accessToken);
    }

    public record LoginDTO(String username, String password) {
    }

    public record AccessTokenDTO(String accessToken) {}
}
