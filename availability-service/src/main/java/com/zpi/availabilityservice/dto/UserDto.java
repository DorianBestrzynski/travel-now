package com.zpi.availabilityservice.dto;

public record UserDto(Long userId, String phoneNumber, String email, String firstName, String lastName) {
    public UserDto()
    {
        this(null, null, null, null, null);
    }
}

