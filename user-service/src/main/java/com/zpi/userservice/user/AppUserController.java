package com.zpi.userservice.user;

import com.zpi.userservice.dto.UserDto;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class AppUserController {
    private final AppUserService appUserService;

    @PostMapping("/users")
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<Long> usersIds) {
        var result = appUserService.getUsers(usersIds);
        return ResponseEntity.ok(result);
    }
}
