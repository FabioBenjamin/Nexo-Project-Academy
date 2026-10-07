package com.nexo.nexo.dto;

public record LoginResponseDTO (
    String accessToken,
    String refreshToken
){}
