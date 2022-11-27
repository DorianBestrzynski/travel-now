package com.zpi.userservice.user;

import com.zpi.userservice.dto.UserDto;
import com.zpi.userservice.mapper.MapStructMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

import static com.zpi.userservice.exceptions.ExceptionInfo.MISSING_USER_ID;
import static com.zpi.userservice.exceptions.ExceptionInfo.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private final MapStructMapper mapper;


    public List<UserDto> getUsers(List<Long> usersIds) {

        List<AppUser> usersList = appUserRepository.findAllById(usersIds);

        return usersList.parallelStream()
                        .map(u -> new UserDto(u.getUserId(),
                                              u.getPhoneNumber(),
                                              u.getEmail(),
                                              u.getFirstName(),
                                              u.getSurname()))
                        .toList();
    }

    @Transactional
    public UserDto editUser(AppUser inputUser) {
        if(inputUser.getUserId() == null) {
            throw new IllegalArgumentException(MISSING_USER_ID);
        }
        if(inputUser.getPassword() != null) {
            inputUser.setPassword(null);
        }
        var currentUser = appUserRepository.findById(inputUser.getUserId()).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));

        mapper.updateAppUser(currentUser, inputUser);

        var savedUser = appUserRepository.save(currentUser);

        return new UserDto(savedUser.getUserId(), savedUser.getPhoneNumber(), savedUser.getEmail(),
                savedUser.getFirstName(), savedUser.getSurname());

    }

    public AppUser getUser(Long userId) {
        return appUserRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }
}
