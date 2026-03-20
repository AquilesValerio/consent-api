package com.sensedia.consentapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsentHistory {

    private LocalDateTime changeAt;
    private String changedBy;
    private String description;
    private ConsentStatus previousStatus;
}
