package com.sensedia.consentapi.dto;

import com.sensedia.consentapi.domain.ConsentStatus;

import java.time.LocalDateTime;

public record ConsentResponseDTO(
        String id,
        String cpf,
        ConsentStatus status,
        LocalDateTime creationDateTime,
        LocalDateTime expirationDateTime,
        String additionalInfo,
        String idempotencyKey

) {
}
