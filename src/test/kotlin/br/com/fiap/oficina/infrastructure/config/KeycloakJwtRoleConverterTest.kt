package br.com.fiap.oficina.infrastructure.config

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt

class KeycloakJwtRoleConverterTest {

    private val converter = KeycloakJwtRoleConverter()

    private fun jwtBuilder(): Jwt.Builder =
        Jwt.withTokenValue("token")
            .header("alg", "none")
            .subject("user")

    @Test
    fun `deve extrair roles de realm e client com prefixo ROLE_`() {
        val jwt = jwtBuilder()
            .claim("realm_access", mapOf("roles" to listOf("ADMIN", "ATENDENTE")))
            .claim("azp", "oficina")
            .claim("resource_access", mapOf("oficina" to mapOf("roles" to listOf("MECANICO"))))
            .build()

        val authorities = converter.convert(jwt).map { it.authority }

        assertTrue(authorities.containsAll(listOf("ROLE_ADMIN", "ROLE_ATENDENTE", "ROLE_MECANICO")))
        assertEquals(3, authorities.size)
    }

    @Test
    fun `deve extrair apenas roles de realm quando nao ha client roles`() {
        val jwt = jwtBuilder()
            .claim("realm_access", mapOf("roles" to listOf("ADMIN")))
            .build()

        val authorities = converter.convert(jwt).map { it.authority }

        assertEquals(listOf("ROLE_ADMIN"), authorities)
    }

    @Test
    fun `deve retornar vazio quando nao ha claims de roles`() {
        val jwt = jwtBuilder().build()

        assertTrue(converter.convert(jwt).isEmpty())
    }

    @Test
    fun `deve ignorar client roles quando azp presente mas sem resource_access`() {
        val jwt = jwtBuilder()
            .claim("azp", "oficina")
            .build()

        assertTrue(converter.convert(jwt).isEmpty())
    }

    @Test
    fun `deve ignorar realm_access quando roles nao for lista`() {
        val jwt = jwtBuilder()
            .claim("realm_access", mapOf("roles" to "ADMIN"))
            .build()

        assertTrue(converter.convert(jwt).isEmpty())
    }

    @Test
    fun `deve remover roles duplicadas entre realm e client`() {
        val jwt = jwtBuilder()
            .claim("realm_access", mapOf("roles" to listOf("ADMIN")))
            .claim("azp", "oficina")
            .claim("resource_access", mapOf("oficina" to mapOf("roles" to listOf("ADMIN"))))
            .build()

        val authorities = converter.convert(jwt).map { it.authority }

        assertEquals(listOf("ROLE_ADMIN"), authorities)
    }
}
