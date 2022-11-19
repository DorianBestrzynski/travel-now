package com.zpi.tripgroupservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JWTVerifierFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = httpServletRequest.getHeader("Authorization");
        String innerCommunicationHeader = httpServletRequest.getHeader("innerCommunication");
        if (innerCommunicationHeader != null) {
            Authentication authentication = new CustomUsernamePasswordAuthenticationToken(null, null, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        logHeaders(httpServletRequest);
        String username = httpServletRequest.getHeader("username");
        List<Map<String, String>> authorities = new ArrayList<>();
        String authoritiesStr = httpServletRequest.getHeader("authorities");
        Long userId = Long.parseLong(httpServletRequest.getHeader("userId"));
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = new HashSet<>();

        if(authoritiesStr != null && !authoritiesStr.isBlank())
            simpleGrantedAuthorities = Arrays.stream(authoritiesStr.split(","))
                                             .distinct()
                                             .map(SimpleGrantedAuthority::new)
                                             .collect(Collectors.toSet());

        Authentication authentication = new CustomUsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorities, userId);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

    private void logHeaders(HttpServletRequest httpServletRequest) {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            logger.info(String.format("Header: %s --- Value: %s", header, httpServletRequest.getHeader(header)));

        }
    }
}