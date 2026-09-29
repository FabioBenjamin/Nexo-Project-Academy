package com.nexo.nexo.config;

import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    // dependency injection
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder){
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken(Long user_id){

        // get the current time
        Instant now = Instant.now();

        // build JWT payload
        // We can add several intel, called claims
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
