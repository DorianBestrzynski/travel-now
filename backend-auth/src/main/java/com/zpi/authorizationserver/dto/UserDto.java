package com.zpi.authorizationserver.dto;

public record UserDto(Long userId, String email, String phoneNumber, String firstName, String surname) {
}

