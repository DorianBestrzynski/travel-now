package com.zpi.userservice.user;

import com.zpi.userservice.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppUserService {
    private final AppUserRepository appUserRepository;


    public List<UserDto> getUsers(List<Long> usersIds) {

        List<AppUser> usersList = appUserRepository.findAllById(usersIds);

        return usersList.parallelStream()
                        .map(u -> new UserDto(u.getUserId(),
                                              u.getUsername(),
                                              u.getFirstName(),
                                              u.getSurname()))
                        .toList();
    }
}
