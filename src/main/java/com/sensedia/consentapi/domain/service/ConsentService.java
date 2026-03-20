package com.sensedia.consentapi.domain.service;

import com.sensedia.consentapi.dto.ConsentRequestDTO;
import com.sensedia.consentapi.dto.ConsentResponseDTO;
import com.sensedia.consentapi.dto.ConsentUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

public interface ConsentService {
    ConsentResponseDTO create(ConsentRequestDTO request,String idempotencyKey);
    Page<ConsentResponseDTO> findAllPaged(PageRequest pageRequest);
    Optional<ConsentResponseDTO> findById(String id);
    void deleteById(String id);
    ConsentResponseDTO update(String id, ConsentUpdateDTO request);
}
