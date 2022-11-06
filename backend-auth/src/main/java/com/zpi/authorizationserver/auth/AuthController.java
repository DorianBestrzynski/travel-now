package com.zpi.authorizationserver.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.zpi.authorizationserver.dto.LoginRequestBodyDto;
import com.zpi.authorizationserver.dto.RegisterRequestDto;
import com.zpi.authorizationserver.dto.UserDto;
import com.zpi.authorizationserver.mapstruct.MapStructMapper;
import com.zpi.authorizationserver.security.JwtTokenFilter;
import com.zpi.authorizationserver.user.AppUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final String jwtSigningSecret;
    private final AuthenticationManager authenticationManager;
    private final AppUserService appUserService;
    private final MapStructMapper mapStructMapper;

    public AuthController(@Value("${jwt.signing-secret}") String jwtSigningSecret,
                          AuthenticationManager authenticationManager,
                          AppUserService appUserService, MapStructMapper mapStructMapper) {
        this.jwtSigningSecret = jwtSigningSecret;
        this.authenticationManager = authenticationManager;
        this.appUserService = appUserService;
        this.mapStructMapper = mapStructMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody LoginRequestBodyDto loginRequestBodyDto) {
        try {
            var email = loginRequestBodyDto.email();
            var password = loginRequestBodyDto.password();
            var authToken = new UsernamePasswordAuthenticationToken(email, password);
            authenticationManager.authenticate(authToken);

            var user = appUserService.getAppUserByEmail(email);
            var jwt = JWT.create()
                         .withClaim(JwtTokenFilter.USERNAME_CLAIM, user.getEmail())
                         .sign(Algorithm.HMAC256(jwtSigningSecret));

            var userDto = mapStructMapper.getUserDtoFromAppUser(user);
            return ResponseEntity.ok()
                                 .header(HttpHeaders.AUTHORIZATION, jwt)
                                 .body(userDto);
        } catch (BadCredentialsException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@RequestBody RegisterRequestDto registerRequestDto) {
        appUserService.registerUser(registerRequestDto);
    }
}