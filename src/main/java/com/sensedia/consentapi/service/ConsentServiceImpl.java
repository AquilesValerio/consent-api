package com.sensedia.consentapi.service;

import com.sensedia.consentapi.domain.Consent;
import com.sensedia.consentapi.domain.ConsentStatus;
import com.sensedia.consentapi.dto.ConsentRequestDTO;
import com.sensedia.consentapi.dto.ConsentResponseDTO;
import com.sensedia.consentapi.dto.ConsentUpdateDTO;
import com.sensedia.consentapi.exception.ResourceNotFoundException;
import com.sensedia.consentapi.mapper.ConsentMapper;
import com.sensedia.consentapi.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {

    private final ConsentRepository repository;
    private final ConsentMapper consentMapper;

    @Override
    public IdempotentResult create(ConsentRequestDTO request, String idempotencyKey) {
        var result = repository.findByIdempotencyKey(idempotencyKey);

        if (result.isPresent()) {
            return new IdempotentResult(consentMapper.entityToDto(result.get()),false);
        }

        Consent entity = consentMapper.dtoToEntity(request);
        entity.setStatus(ConsentStatus.ACTIVE);
        entity.setCreationDateTime(LocalDateTime.now());
        entity.setIdempotencyKey(idempotencyKey);
        entity = repository.save(entity);

        return new IdempotentResult (consentMapper.entityToDto(entity),true);
    }

    @Override
    public Page<ConsentResponseDTO> findAllPaged(PageRequest pageRequest) {
        Page<Consent> list = repository.findAll(pageRequest);
        return list.map((x) -> consentMapper.entityToDto(x));
    }

    @Override
    public Optional<ConsentResponseDTO> findById(String id) {
        Consent entity = existsConsent(id);
        return Optional.ofNullable(consentMapper.entityToDto(entity));
    }

    @Override
    public void deleteById(String id) {
        Consent consent = existsConsent(id);

        if (consent.getStatus() == ConsentStatus.REVOKED) {
            throw new IllegalStateException("Consentimento já está revogado");
        }

        consent.setStatus(ConsentStatus.REVOKED);
        repository.save(consent);
    }

    @Override
    public ConsentResponseDTO update(String id, ConsentUpdateDTO request) {
        Consent consent = existsConsent(id);

        if (consent.getStatus() == ConsentStatus.REVOKED) {
            throw new IllegalStateException("Não é possível atualizar um consentimento revogado.");
        }

        consent.setAdditionalInfo(request.additionalInfo());
        consent.setExpirationDateTime(request.expirationDateTime());
        consent = repository.save(consent);


        return consentMapper.entityToDto(consent);
    }

    private Consent existsConsent(String id) {
        return repository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Consentimento não encontrado"));
    }

}
