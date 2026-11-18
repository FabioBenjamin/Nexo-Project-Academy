package com.nexo.dto;

// Record: Spring automatically generate the getters setters and constructor
// Record Difference: Once you create the object, the attributes cannot be changed
public record ErrorResponseDTO(
        String error,
        String message,
        Integer status
){}
