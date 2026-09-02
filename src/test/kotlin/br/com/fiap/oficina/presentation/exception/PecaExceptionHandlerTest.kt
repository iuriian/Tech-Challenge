package br.com.fiap.oficina.presentation.exception

import br.com.fiap.oficina.domain.exception.PecaNaoEncontradoException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class PecaExceptionHandlerTest {
    private val handler = PecaExceptionHandler()

    @Test
    fun `deve retornar 404 com mensagem da excecao`() {
        val response = handler.handlePecaNaoEncontrada(PecaNaoEncontradoException.porCodigo("XPTO"))

        assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        assertEquals("Peça não encontrada com o código: XPTO", response.body?.get("message"))
    }

    @Test
    fun `deve retornar 409 quando peca ja cadastrada`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException("Peça já cadastrada"))

        assertEquals(HttpStatus.CONFLICT, response.statusCode)
        assertEquals("Peça já cadastrada", response.body?.get("message"))
    }

    @Test
    fun `deve retornar 400 para argumento ilegal generico`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException("Quantidade inválida"))

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Quantidade inválida", response.body?.get("message"))
    }

    @Test
    fun `deve retornar 400 com mensagem padrao quando argumento ilegal nao tem mensagem`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException())

        assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        assertEquals("Requisição inválida", response.body?.get("message"))
    }
}
