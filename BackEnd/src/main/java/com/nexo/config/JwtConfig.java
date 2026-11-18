package com.nexo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class JwtConfig {

    @Value("${jwt.key}")
    private String jwtKey;

    private SecretKey secretKey(){
        return new SecretKeySpec(
                jwtKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
    }

    // Creates a JWT Encoder obj that generates a Jwt
    @Bean
    public JwtEncoder jwtEncoder() {

        // return an object capable of sign and codify JWTs
        return NimbusJwtEncoder.withSecretKey(secretKey()).build();
    }

    // Creates a Jwt Decoder obj that valid Jwt tokens
    @Bean
    public JwtDecoder jwtDecoder(){
        return NimbusJwtDecoder
                .withSecretKey(secretKey())
                .build();
    }
}