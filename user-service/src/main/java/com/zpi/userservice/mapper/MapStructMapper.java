package com.zpi.userservice.mapper;

import com.zpi.userservice.user.AppUser;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructMapper {

    void updateAppUser(@MappingTarget AppUser finalUser, AppUser inputUser);
}
