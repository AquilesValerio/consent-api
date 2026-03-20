package com.sensedia.consentapi.mapper;

import com.sensedia.consentapi.domain.Consent;
import com.sensedia.consentapi.dto.ConsentRequestDTO;
import com.sensedia.consentapi.dto.ConsentResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConsentMapper {

    Consent dtoToEntity(ConsentRequestDTO requestDTO);
    ConsentResponseDTO entityToDto(Consent entity);
}
