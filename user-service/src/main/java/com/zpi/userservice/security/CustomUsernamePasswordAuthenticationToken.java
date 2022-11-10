package com.zpi.userservice.security;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
public class CustomUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    @Getter
    @Setter
    private Long userId;

    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials, Long userId) {
        super(principal, credentials);
        this.userId = userId;
    }

    public CustomUsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, Long userId) {
        super(principal, credentials, authorities);
        this.userId = userId;
    }


}
