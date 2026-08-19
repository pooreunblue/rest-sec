package org.example.restsec.config;

import org.example.restsec.auth.JwtAuthenticationFilter;
import org.example.restsec.auth.RestAccessDeniedHandler;
import org.example.restsec.auth.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(Customizer.withDefaults()) // CORS
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        ex -> ex
                                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                                .accessDeniedHandler(new RestAccessDeniedHandler())
                )
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(
                                        "/auth/login",
                                        "/", "/index.html",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/chair/**")
                                .permitAll()
                                .requestMatchers(HttpMethod.POST, "/chair/**")
                                .authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/chair/**")
                                .hasRole("ADMIN")
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
        // -> UsernamePasswordAuthenticationToken
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://127.0.0.1:5500"));
        config.setAllowedMethods(List.of("GET", "POST", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
//        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
