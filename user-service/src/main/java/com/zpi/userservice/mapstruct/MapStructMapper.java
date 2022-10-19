package com.zpi.userservice.mapstruct;

import com.zpi.userservice.dto.UserDto;
import com.zpi.userservice.user.AppUser;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructMapper {

    UserDto getUserDtoFromAppUser(AppUser appUser);
}
