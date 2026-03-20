package com.sensedia.consentapi.service;

import com.sensedia.consentapi.domain.Consent;
import com.sensedia.consentapi.domain.ConsentStatus;
import com.sensedia.consentapi.dto.ConsentRequestDTO;
import com.sensedia.consentapi.dto.ConsentResponseDTO;
import com.sensedia.consentapi.exception.ResourceNotFoundException;
import com.sensedia.consentapi.mapper.ConsentMapper;
import com.sensedia.consentapi.repository.ConsentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsentServiceImpl - Testes Unitários")
class ConsentServiceImplTest {

    @Mock
    private ConsentRepository repository;

    @Mock
    private ConsentMapper consentMapper;

    @InjectMocks
    private ConsentServiceImpl service;

    private ConsentRequestDTO request;
    private Consent consent;
    private ConsentResponseDTO responseDTO;
    private String idempotencyKey;

    @BeforeEach
    void setUp() {
        idempotencyKey = UUID.randomUUID().toString();

        request = new ConsentRequestDTO(
                "529.982.247-25",
                LocalDateTime.now().plusDays(30),
                "Teste"
        );

        consent = new Consent();
        consent.setId(UUID.randomUUID().toString());
        consent.setCpf("529.982.247-25");
        consent.setStatus(ConsentStatus.ACTIVE);
        consent.setCreationDateTime(LocalDateTime.now());
        consent.setIdempotencyKey(idempotencyKey);

        responseDTO = new ConsentResponseDTO(
                consent.getId(),
                consent.getCpf(),
                consent.getStatus(),
                consent.getCreationDateTime(),
                null,
                null,
                consent.getIdempotencyKey()
        );
    }

    @Test
    @DisplayName("Deve criar consentimento com sucesso")
    void shouldCreateConsent() {
        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(consentMapper.dtoToEntity(request)).thenReturn(consent);
        when(repository.save(any())).thenReturn(consent);
        when(consentMapper.entityToDto(consent)).thenReturn(responseDTO);

        IdempotentResult result = service.create(request, idempotencyKey);

        // Verifica que foi criação nova
        assertThat(result).isNotNull();
        assertThat(result.created()).isTrue();
        assertThat(result.data().cpf()).isEqualTo("529.982.247-25");
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Deve retornar consentimento existente para chave idempotente")
    void shouldReturnExistingForIdempotentKey() {
        when(repository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(consent));
        when(consentMapper.entityToDto(consent)).thenReturn(responseDTO);

        IdempotentResult result = service.create(request, idempotencyKey);

        // Verifica que foi idempotência
        assertThat(result).isNotNull();
        assertThat(result.created()).isFalse();
        assertThat(result.data().id()).isEqualTo(consent.getId());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar consentimento inexistente")
    void shouldThrowWhenConsentNotFound() {
        when(repository.findById("id-invalido")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById("id-invalido"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Deve revogar consentimento com sucesso")
    void shouldRevokeConsent() {
        when(repository.findById(consent.getId())).thenReturn(Optional.of(consent));
        when(repository.save(any())).thenReturn(consent);

        service.deleteById(consent.getId());

        assertThat(consent.getStatus()).isEqualTo(ConsentStatus.REVOKED);
        verify(repository).save(consent);
    }

    @Test
    @DisplayName("Não deve revogar consentimento já revogado")
    void shouldThrowWhenAlreadyRevoked() {
        consent.setStatus(ConsentStatus.REVOKED);
        when(repository.findById(consent.getId())).thenReturn(Optional.of(consent));

        assertThatThrownBy(() -> service.deleteById(consent.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("revogado");
    }
}