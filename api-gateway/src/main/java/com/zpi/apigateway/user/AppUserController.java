package com.zpi.apigateway.user;

import com.zpi.apigateway.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
