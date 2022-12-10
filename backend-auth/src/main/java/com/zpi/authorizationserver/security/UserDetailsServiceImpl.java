package com.zpi.authorizationserver.security;


import com.zpi.authorizationserver.exceptions.ApiPermissionException;
import com.zpi.authorizationserver.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService
{
    private final AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var appUser = appUserRepository.findAppUserByEmail(username).orElseThrow(() -> new ApiPermissionException("Incorrect email or password. Permission denied"));

        return new User(appUser.getEmail(), appUser.getPassword().getHashedPassword(), Collections.emptyList());
    }
}