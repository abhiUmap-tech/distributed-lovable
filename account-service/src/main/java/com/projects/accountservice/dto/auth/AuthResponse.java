package com.projects.accountservice.dto.auth;

public record AuthResponse(
        String token,
        UserProfileResponse user) {
}
