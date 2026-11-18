package com.nexo.dto;

public record ChangePasswordRequest(
        String oldPassword,
        String newPassword,
        String confirmNewPassword
) {
}
