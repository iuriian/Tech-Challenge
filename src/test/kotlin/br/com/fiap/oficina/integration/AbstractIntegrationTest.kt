package br.com.fiap.oficina.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

/**
 * Base dos testes de integração. Sobe o contexto Spring completo contra um
 * PostgreSQL real (Testcontainers), com o schema e o seed aplicados pelo Flyway,
 * exercitando toda a pilha: HTTP (MockMvc) → controller → service → adapter JPA
 * → banco.
 *
 * - O container estático é compartilhado entre todas as classes de teste e o
 *   contexto Spring é cacheado, então o banco sobe uma única vez.
 * - [Transactional] faz cada método de teste rodar em uma transação que é
 *   revertida ao final, mantendo o seed do Flyway íntegro entre os testes.
 * - A autenticação é simulada com `@WithMockUser`, então o JwtDecoder OAuth2
 *   nunca é acionado.
 *
 * Requer um Docker em execução. Em daemons recentes (Docker Desktop, API >= 1.40)
 * o build define `DOCKER_API_VERSION=1.43` para o cliente do Testcontainers
 * (ver build.gradle.kts).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
@Transactional
abstract class AbstractIntegrationTest {

    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    companion object {
        @Container
        @ServiceConnection
        @JvmStatic
        val postgres: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:16-alpine")
    }
}
