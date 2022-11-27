package com.zpi.userservice.dto;

public record UserDto(Long userId, String phoneNumber, String email, String firstName, String lastName) {
}

