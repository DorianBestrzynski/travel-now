package com.zpi.availabilityservice.dto;

public record UserDto(Long userId, String username, String firstName, String lastName) {
    public UserDto()
    {
        this(null, null, null, null);
    }
}

