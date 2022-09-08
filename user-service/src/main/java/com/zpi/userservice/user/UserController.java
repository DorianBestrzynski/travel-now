package com.zpi.userservice.user;

import lombok.RequiredArgsConstructor;

import org.apache.catalina.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    private final UserRepository userRepository;


    @GetMapping("/test")
    public String createUser(){
        AppUser appUser = new AppUser("aa", "bb","aa","bb", LocalDate.now(), LocalDateTime.now(), new Password("Password"));
        appUser.getPassword().setAppUser(appUser);
        userRepository.save(appUser);
        return "Hello Boba";
    }

    @GetMapping()
    public List<AppUser> getAllUsers(){
        return userRepository.findAll();
    }





}
