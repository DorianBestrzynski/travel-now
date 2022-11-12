package com.zpi.authorizationserver.user;

import com.zpi.authorizationserver.dto.RegisterRequestDto;
import com.zpi.authorizationserver.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;


    public List<UserDto> getUsers(List<Long> usersIds) {

        List<AppUser> usersList = appUserRepository.findAllById(usersIds);

        return usersList.parallelStream()
                        .map(u -> new UserDto(u.getUserId(),
                                              u.getEmail(),
                                              u.getUsername(),
                                              u.getFirstName(),
                                              u.getSurname()))
                        .toList();
    }

    public AppUser getAppUserByEmail(String email) {
        return appUserRepository.findAppUserByEmail(email).orElseThrow();
    }

    public void registerUser(RegisterRequestDto registerRequestDto) {
        var encodedPassword = passwordEncoder.encode(registerRequestDto.password());
        var password = new Password(encodedPassword);
        var user = new AppUser(registerRequestDto.email(),
                               registerRequestDto.username(),
                               registerRequestDto.firstName(),
                               registerRequestDto.surname(),
                               registerRequestDto.birthday(),
                               LocalDateTime.now(),
                               password);
        password.setAppUser(user);
        appUserRepository.save(user);
    }
}
