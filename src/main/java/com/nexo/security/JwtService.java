package com.nexo.nexo.security;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    // dependency injection
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public JwtService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder){
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    // Generate access token
    public String generateToken(Long user_id){

        // get the current time
        Instant now = Instant.now();

        // build JWT payload
        // We can add various intel, called claims
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(user_id.toString())
                .issuedAt(now)  // represent the created time
                .expiresAt(now.plusSeconds(900))
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims)) // build a Jwt token
                .getTokenValue();
    }


}
