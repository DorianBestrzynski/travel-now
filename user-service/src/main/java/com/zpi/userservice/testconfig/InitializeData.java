package com.zpi.userservice.testconfig;

import com.zpi.userservice.user.AppUser;
import com.zpi.userservice.user.AppUserRepository;
import com.zpi.userservice.user.Password;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitializeData {
    @Value("${spring.profiles.active:default}")
    private String profile;

    private final AppUserRepository appUserRepository;

    @PostConstruct
    public void addTripGroupsAndUserGroupsAndInvitations() {
        if (!profile.equals("test"))
            return;

        var user = List.of(
                new AppUser("DorBest", "Dorian@Dorian", "Dorian", "Best", LocalDate.now(), new Password("test")),
                new AppUser("Kaj", "Kaj@Kaj", "Kaj", "Kaj", LocalDate.now(), new Password("test")),
                new AppUser("Piotr", "Piotr@Piotr", "Piotr", "Piotr", LocalDate.now(), new Password("test")),
                new AppUser("Krzy", "Krzy@Krzy", "Krzy", "Krzy", LocalDate.now(), new Password("test"))
        );

        user.forEach(appUser -> appUser.getPassword().setAppUser(appUser));

        appUserRepository.saveAll(user);
    }
}
