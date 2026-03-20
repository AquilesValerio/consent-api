package com.sensedia.consentapi.dto;

import com.sensedia.consentapi.validation.ValidCpf;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ConsentRequestDTO(
        @ValidCpf
        String cpf,
        LocalDateTime expirationDateTime,
        @Size(min = 1, max = 50)
        String additionalInfo
) {
}
