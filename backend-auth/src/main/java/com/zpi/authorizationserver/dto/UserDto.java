package com.zpi.authorizationserver.dto;

public record UserDto(Long userId, String email, String username, String firstName, String surname) {
}

