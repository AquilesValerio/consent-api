# consent-api

API REST para gerenciamento de consentimentos no ecossistema **Open Insurance Brasil (OPIN)**.

---

## Tecnologias

- Java 21
- Spring Boot 3.3.0
- MongoDB
- MapStruct
- Lombok
- Springdoc (Swagger)
- Testcontainers
- JUnit 5 + Mockito
- Docker

---

## Como executar

### Opção 1 — Docker Compose (recomendado)

```bash
docker-compose up --build
```

A API ficará disponível em: `http://localhost:8080`

---

### Opção 2 — Maven local

```bash
# Configure o application.properties com sua URI do MongoDB
./mvnw spring-boot:run
```

---

## Documentação (Swagger)

```
http://localhost:8080/swagger-ui.html
```

---

## Endpoints

| Método | Rota           | Descrição                         |
| ------ | -------------- | --------------------------------- |
| POST   | /consents      | Criar consentimento (idempotente) |
| GET    | /consents      | Listar todos (paginado)           |
| GET    | /consents/{id} | Buscar por ID                     |
| PUT    | /consents/{id} | Atualizar                         |
| DELETE | /consents/{id} | Revogar                           |

---

## Idempotência

O endpoint `POST /consents` é idempotente via header `X-Idempotency-Key`:

- **Primeira chamada** → `201 Created`
- **Chamadas repetidas com a mesma chave** → `200 OK`

---

## Testes

```bash
./mvnw test
```

> Os testes de integração sobem automaticamente um container MongoDB via Testcontainers. É necessário ter o Docker disponível.

---

## Estrutura do projeto

```
src/main/java/com/sensedia/consentapi/
├── controller/     # Endpoints REST
├── domain/         # Entidades e enums
├── dto/            # Request/Response DTOs
├── exception/      # Tratamento de erros
├── mapper/         # MapStruct
├── repository/     # MongoDB Repository
├── service/        # Regras de negócio
└── validation/     # Validação de CPF
```
