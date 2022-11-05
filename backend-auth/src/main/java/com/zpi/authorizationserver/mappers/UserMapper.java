package com.zpi.authorizationserver.mappers;

import com.zpi.authorizationserver.dto.UserDto;
import com.zpi.authorizationserver.user.AppUser;
import com.zpi.authorizationserver.user.Password;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "user.userId", target = "userID")
    @Mapping(source = "user.email", target = "login")
    @Mapping(source = "token", target = "token")
    @Mapping(target = "password", ignore = true)
    UserDto toUserDto(AppUser user, String token);

    @Mapping(source = "encodedPassword", target = "password")
    AppUser toAuthUser(UserDto userDto, String encodedPassword);

    default Password map(String value) {
        return new Password(value);
    }
}
