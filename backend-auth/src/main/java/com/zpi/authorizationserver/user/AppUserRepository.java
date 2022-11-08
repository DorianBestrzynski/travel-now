package com.zpi.authorizationserver.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
//    @Query("SELECT new com.zpi.userservice.dto.UserDto(u.userId, u.email, u.username, u.firstName, u.surname) " +
//            "FROM AppUser u WHERE u.email = ?1")
//    Optional<UserDto> findAppUserByEmail(String email);
    Optional<AppUser> findAppUserByEmail(String email);
}
