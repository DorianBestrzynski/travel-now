package com.zpi.auth.authorizationserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class UserAuthenticationProvider {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String login = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<AuthUser> oUser = userRepository.findByLogin(login);

        if (oUser.isEmpty()) {
            return null;
        }

        AuthUser user = oUser.get();

        if (passwordEncoder.matches(CharBuffer.wrap(password), user.getPassword())) {
            return UsernamePasswordAuthenticationToken.authenticated(login, password, Collections.emptyList());
        }

        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
