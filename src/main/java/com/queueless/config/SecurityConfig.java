
package com.queueless.config;

import java.util.List;
import java.util.Collection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(
                    List.of("http://localhost:5173"));
                config.setAllowedMethods(
                    List.of("GET", "POST", "PATCH", "PUT",
                            "DELETE", "OPTIONS"));
                config.setAllowedHeaders(
                    List.of("Authorization", "Content-Type"));
                return config;
            }))
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers(
            			    "/api/users/register",
            			    "/api/users/login",
            			    "/api/categories",
            			    "/api/categories/**",
            			    "/api/services",
            			    "/api/services/**",
            			    "/api/queue/**",
            			    "/api/queues/**",
            			    "/actuator/health"
            			).permitAll()
            			.anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(
                    jwtAuthenticationConverter())));

        return http.build();
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken>
            jwtAuthenticationConverter() {

        return jwt -> {
            String role = jwt.getClaimAsString("role");

            Collection<GrantedAuthority> authorities =
                    role == null
                    ? List.of()
                    : List.of(new SimpleGrantedAuthority(
                            "ROLE_" + role));

            return new JwtAuthenticationToken(
                    jwt, authorities, jwt.getSubject());
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
