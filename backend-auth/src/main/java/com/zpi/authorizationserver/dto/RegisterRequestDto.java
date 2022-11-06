package com.zpi.authorizationserver.dto;

import java.time.LocalDate;

public record RegisterRequestDto(String email, String username, String password, String firstName, String surname, LocalDate birthday) {
}