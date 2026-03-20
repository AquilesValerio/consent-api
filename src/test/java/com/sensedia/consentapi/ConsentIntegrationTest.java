package com.sensedia.consentapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sensedia.consentapi.dto.ConsentRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("Consent API - Testes de Integração")
class ConsentIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private com.sensedia.consentapi.repository.ConsentRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    private ConsentRequestDTO buildRequest() {
        return new ConsentRequestDTO(
                "529.982.247-25",
                LocalDateTime.now().plusDays(30),
                "Teste de integração"
        );
    }

    @Test
    @DisplayName("POST /consents - deve criar consentimento e retornar 201")
    void shouldCreateConsent() throws Exception {
        mockMvc.perform(post("/consents")
                        .header("x-idempotency-key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.cpf").value("529.982.247-25"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("POST /consents - deve retornar 200 em requisição idempotente")
    void shouldReturn200ForIdempotentRequest() throws Exception {
        String key = UUID.randomUUID().toString();

        mockMvc.perform(post("/consents")
                        .header("x-idempotency-key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/consents")
                        .header("x-idempotency-key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /consents - deve retornar 400 para CPF inválido")
    void shouldReturn400ForInvalidCpf() throws Exception {
        var request = new ConsentRequestDTO(
                "000.000.000-00",
                LocalDateTime.now().plusDays(30),
                "Teste"
        );

        mockMvc.perform(post("/consents")
                        .header("x-idempotency-key", UUID.randomUUID().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /consents/{id} - deve retornar 404 para ID inexistente")
    void shouldReturn404ForUnknownId() throws Exception {
        mockMvc.perform(get("/consents/id-inexistente"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /consents/{id} - deve revogar consentimento")
    void shouldRevokeConsent() throws Exception {
        String key = UUID.randomUUID().toString();

        String body = mockMvc.perform(post("/consents")
                        .header("x-idempotency-key", key)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andReturn().getResponse().getContentAsString();

        String id = objectMapper.readTree(body).get("id").asText();

        mockMvc.perform(delete("/consents/" + id))
                .andExpect(status().isNoContent());
    }
}