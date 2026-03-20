package com.sensedia.consentapi.service;

import com.sensedia.consentapi.dto.ConsentResponseDTO;

public record IdempotentResult(ConsentResponseDTO data, boolean created) {
}
