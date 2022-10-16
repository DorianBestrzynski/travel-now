package com.zpi.transportservice.transportservice.mapper;

import com.zpi.transportservice.transportservice.dto.UserTransportDto;
import com.zpi.transportservice.transportservice.transport.UserTransport;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface MapStructMapper {

    void mapFromUserTransportDtoToUserTransport(@MappingTarget UserTransport userTransport, UserTransportDto userTransportDto);

}
