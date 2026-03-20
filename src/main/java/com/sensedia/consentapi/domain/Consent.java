package com.sensedia.consentapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "consents")
public class Consent {

    @Id
    private String id;
    private String cpf;
    private ConsentStatus status;
    private LocalDateTime creationDateTime;
    private LocalDateTime expirationDateTime;
    private String additionalInfo;
    private String idempotencyKey;

    @Builder.Default
    private List<ConsentHistory> history = new ArrayList<>();

    public static Consent create(String cpf, LocalDateTime expirationDateTime, String additionalInfo, String idempotencyKey) {
        var consent = Consent.builder()
                .id(UUID.randomUUID().toString())
                .cpf(cpf)
                .status(ConsentStatus.ACTIVE)
                .creationDateTime(LocalDateTime.now())
                .expirationDateTime(expirationDateTime)
                .additionalInfo(additionalInfo)
                .idempotencyKey(idempotencyKey)
                .build();
        consent.addHistory("SYSTEM", "Consentimento criado");
        return consent;
    }

    public void addHistory(String changedBy, String description) {
        if (this.history == null) {
            history = new ArrayList<>();
            history.add(ConsentHistory.builder()
                    .changedBy(changedBy)
                    .description(description)
                    .changeAt(LocalDateTime.now())
                    .previousStatus(status)
                    .build());
        }
    }


}
