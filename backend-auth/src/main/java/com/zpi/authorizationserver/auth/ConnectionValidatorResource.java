package com.zpi.authorizationserver.auth;

import com.zpi.authorizationserver.dto.ConnValidationResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/validateToken")
public class ConnectionValidatorResource {


    @PostMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ConnValidationResponse> validatePost() {
        return ResponseEntity.ok(ConnValidationResponse.builder()
                                                       .status("OK")
                                                       .methodType(HttpMethod.POST.name())
                                                       .isAuthenticated(true)
                                                       .build());
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ConnValidationResponse> validateGet(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        String token = (String) request.getAttribute("jwt");
        Long userId = (Long) request.getAttribute("userId");
        List<GrantedAuthority> grantedAuthorities = ((Set<GrantedAuthority>) request.getAttribute("authorities")).stream().toList();

        return ResponseEntity.ok(ConnValidationResponse.builder()
                                                       .status("OK")
                                                       .methodType(HttpMethod.GET.name())
                                                       .username(username)
                                                       .token(token)
                                                       .authorities(grantedAuthorities)
                                                       .userId(userId)
                                                       .isAuthenticated(true)
                                                       .build());
    }


    @PostMapping(value = "/whitelisted", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ConnValidationResponse> validateWhitelistedPost() {
        return ResponseEntity.ok(ConnValidationResponse.builder()
                                                       .status("OK")
                                                       .methodType(HttpMethod.POST.name())
                                                       .build());
    }

    @GetMapping(value = "/whitelisted", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ConnValidationResponse> validateWhitelistedGet(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return ResponseEntity.ok(ConnValidationResponse.builder()
                                                       .status("OK")
                                                       .methodType(HttpMethod.GET.name())
                                                       .username(username)
                                                       .build());
    }

}
