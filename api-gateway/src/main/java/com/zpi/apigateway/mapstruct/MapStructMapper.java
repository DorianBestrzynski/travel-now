package com.zpi.apigateway.mapstruct;

import com.zpi.apigateway.dto.UserDto;
import com.zpi.apigateway.user.AppUser;
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
