//package com.zpi.apigateway.security;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RouteConfiguration {
//
//    @Bean
//    public RouteLocator routes(RouteLocatorBuilder builder, AuthenticationPrefilter authFilter) {
//        return builder.routes()
//                      .route("auth-service-route", r -> r.path("/api/auth/**")
//                                                         .filters(f ->
////                                                                          f.rewritePath("/api/auth/login/(?<segment>/?.*)", "$\\{segment}")
//                                                                           f.filter(authFilter.apply(new AuthenticationPrefilter.Config())))
//                                                         .uri("lb://authentication-service"))
////                      .route("user-service-route", r -> r.path("/api/v1/user/**")
////                                                         .filters(f ->
////                                                                          f.rewritePath("/api/v1/user/(?<segment>/?.*)", "$\\{segment}")
////                                                                           .filter(authFilter.apply(
////                                                                                   new AuthenticationPrefilter.Config())))
////                                                         .uri("lb://user/"))
//                      .build();
//    }
//
//}