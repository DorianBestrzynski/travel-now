package com.zpi.authorizationserver.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    public static final String USERNAME_CLAIM = "username";

    private final String jwtSigningSecret;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtTokenFilter(@Value("${jwt.signing-secret}") String jwtSigningSecret,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtSigningSecret = jwtSigningSecret;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = authHeader.split(" ")[1].trim();

        try {
            var verifier = JWT.require(Algorithm.HMAC256(jwtSigningSecret)).build();
            var jwt = verifier.verify(token);
            setAuthentication(jwt, request);
        } catch (JWTVerificationException exception) {
            // TODO: Log that token verification failed
        }

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(DecodedJWT jwt, HttpServletRequest request) {
        Claim usernameClaim = jwt.getClaim(USERNAME_CLAIM);
        if (usernameClaim != null) {
            var username = usernameClaim.asString();
            var userDetails = userDetailsService.loadUserByUsername(username);
            var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            request.setAttribute("username", userDetails.getUsername());
            request.setAttribute("jwt", jwt.getToken());
            request.setAttribute("authorities", userDetails.getAuthorities());
        }
    }
}
