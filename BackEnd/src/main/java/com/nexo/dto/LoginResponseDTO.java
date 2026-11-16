package com.nexo.dto;

public record LoginResponseDTO (
    String accessToken,
    String refreshToken,
    String username,
    String userId
){}
