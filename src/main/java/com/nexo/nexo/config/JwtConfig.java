package com.nexo.nexo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    @Value("${JWT_KEY}")
    private String jwtKey;

    @Bean
    // return an object capable of sign and codify JWTs
    public JwtEncoder jwtEncoder(){
        return NimbusJwtEncoder.withSecretKey(
                new ImmutableSecret<>(jwtKey.getBytes(StandardCharsets.UTF_8))
        ).build();
    }
}
