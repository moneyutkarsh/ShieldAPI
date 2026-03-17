package com.shieldapi.mapper;

import com.shieldapi.dto.UserRequestDTO;
import com.shieldapi.dto.UserResponseDTO;
import com.shieldapi.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponse(User user);

    User toEntity(UserRequestDTO request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UserRequestDTO request, @MappingTarget User user);
}
