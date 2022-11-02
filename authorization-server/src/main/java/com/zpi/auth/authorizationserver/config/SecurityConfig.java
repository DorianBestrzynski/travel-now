package com.zpi.auth.authorizationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.UUID;

@EnableWebSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        http.exceptionHandling(
                exceptions -> exceptions.authenticationEntryPoint((new LoginUrlAuthenticationEntryPoint("/login"))));
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest()
                                                         .authenticated())
            .formLogin(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                                                            // client-id and client-secret that must be used from all the OAuth2 clients
                                                            .clientId("messages-client")
                                                            .clientSecret("$2a$12$/xdT4GByOtITcHq7SGtV.ORBMc.Vh3gu3nWz1IDuKxCiBBmG9aiLG")
                                                            // the Basic authentication method will be used between backend-client and backend-auth
                                                            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                                            // grant types to be used
                                                            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                                                            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                                                            // permitted redirect URI after the authentication is successful
                                                            .redirectUri("http://backend-client:8083/login/oauth2/code/messages-client-oidc")
                                                            .redirectUri("http://backend-client:8083/authorized")
                                                            // acceptable scopes for the authorization
                                                            .scope(OidcScopes.OPENID)
                                                            .scope("message.read")
                                                            .scope("message.write")
                                                            .build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    /**
     * Acceptable URL of the authorization server
     */
    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                               .issuer("http://backend-auth:8081")
                               .build();
    }
}
