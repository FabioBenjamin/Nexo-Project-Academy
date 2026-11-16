package com.nexo.dto;

public record ResetPasswordRequest(
        String email,
        String code,
        String newPassword,
        String confirmNewPassword
) {}