package com.zpi.authorizationserver.mappers;

import com.zpi.authorizationserver.dto.UserDto;
import com.zpi.authorizationserver.user.AppUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "email", target = "login")
    @Mapping(source = "token", target = "token")
    @Mapping(target = "password", ignore = true)
    UserDto toUserDto(AppUser user, String token);

    @Mapping(source = "encodedPassword", target = "password")
    AppUser toAuthUser(UserDto userDto, String encodedPassword);
}
