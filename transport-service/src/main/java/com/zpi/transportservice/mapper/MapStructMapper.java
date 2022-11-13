package com.zpi.transportservice.mapper;

import com.zpi.transportservice.dto.UserTransportDto;
import com.zpi.transportservice.transport.UserTransport;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructMapper {

    void mapFromUserTransportDtoToUserTransport(@MappingTarget UserTransport userTransport, UserTransportDto userTransportDto);

}
