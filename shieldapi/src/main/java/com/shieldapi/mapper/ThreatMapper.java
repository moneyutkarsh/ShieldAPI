package com.shieldapi.mapper;

import com.shieldapi.dto.ThreatRequestDTO;
import com.shieldapi.dto.ThreatResponseDTO;
import com.shieldapi.model.Threat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ThreatMapper {

    ThreatMapper INSTANCE = Mappers.getMapper(ThreatMapper.class);

    Threat toEntity(ThreatRequestDTO requestDTO);

    ThreatResponseDTO toResponseDTO(Threat threat);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "detectedAt", ignore = true)
    void updateEntityFromDto(ThreatRequestDTO dto, @MappingTarget Threat entity);
}
