package br.com.fiap.oficina.presentation.exception

import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.exception.VeiculoNaoEncontradoException
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import kotlin.test.assertEquals

class VeiculoExceptionHandlerTest {
    private val handler = VeiculoExceptionHandler()

    @Test
    fun `handleNotFound deve retornar 404 para VeiculoNaoEncontradoException`() {
        val response = handler.handleNotFound(VeiculoNaoEncontradoException("Veículo não encontrado"))

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Veículo não encontrado", response.body?.get("message"))
    }

    @Test
    fun `handleNotFound deve retornar 404 para ClienteNaoEncontradoException`() {
        val response = handler.handleNotFound(ClienteNaoEncontradoException("Cliente não encontrado"))

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Cliente não encontrado", response.body?.get("message"))
    }

    @Test
    fun `handleNotFound deve usar mensagem padrao quando exception nao possui mensagem`() {
        val response = handler.handleNotFound(RuntimeException())

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Recurso não encontrado", response.body?.get("message"))
    }

    @Test
    fun `handleIllegalArgument deve retornar 409 quando mensagem contem cadastrado`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException("Veículo já cadastrado com essa placa"))

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Veículo já cadastrado com essa placa", response.body?.get("message"))
    }

    @Test
    fun `handleIllegalArgument deve retornar 409 quando mensagem contem Ja existe`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException("Já existe um veículo com essa placa"))

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
    }

    @Test
    fun `handleIllegalArgument deve retornar 400 quando mensagem nao indica duplicidade`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException("Placa inválida"))

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Placa inválida", response.body?.get("message"))
    }

    @Test
    fun `handleIllegalArgument deve retornar 400 e mensagem padrao quando exception nao possui mensagem`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException())

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Requisição inválida", response.body?.get("message"))
    }
}
