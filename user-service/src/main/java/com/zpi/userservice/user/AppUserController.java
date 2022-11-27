package com.zpi.userservice.user;

import com.zpi.userservice.dto.UserDto;
import com.zpi.userservice.mapper.MapStructMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class AppUserController {
    private final AppUserService appUserService;
    private final AppUserRepository appUserRepository;

    @PostMapping("/users")
    public ResponseEntity<List<UserDto>> getUsersByIds(@RequestBody List<Long> usersIds) {
        var result = appUserService.getUsers(usersIds);
        return ResponseEntity.ok(result);
    }

    @PatchMapping()
    public ResponseEntity<UserDto> editUser(@RequestBody AppUser appUser) {
        var result = appUserService.editUser(appUser);
        return ResponseEntity.ok(result);
    }

    @GetMapping()
    public ResponseEntity<AppUser> getAppUser(@RequestParam Long userId) {
        var result = appUserService.getUser(userId);
        return ResponseEntity.ok(result);
    }



    @GetMapping("/test")
    public String test() {
        return "Get no body";
    }

    @GetMapping("/sampleData")
    public List<AppUser> createSampleUsers(){
        var user1 = new AppUser("firstUser", "user@user.com", "Adam", "Boruc", LocalDate.now(), null);
        var user2 = new AppUser("secondUser", "user2@user.com", "X", "Y", LocalDate.now(), null);
        var user3 = new AppUser("thirdUser", "user3@user.com", "Z", "K", LocalDate.now(), null);
        var user4 = new AppUser("fourthUser", "user4@user.com", "T", "Y", LocalDate.now(), null);
        return appUserRepository.saveAll(List.of(user1, user2, user3, user4));
    }
}
