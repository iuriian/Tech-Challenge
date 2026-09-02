package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.application.dto.ServicoRequest
import br.com.fiap.oficina.application.dto.ServicoResponse
import br.com.fiap.oficina.application.service.ServicoService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.math.BigDecimal
import java.util.UUID

class ServicoControllerTest {
    private val service = mock(ServicoService::class.java)

    private val controller =
        ServicoController(
            service = service,
        )

    private val id = UUID.randomUUID()

    private val response =
        ServicoResponse(
            id = id,
            descricao = "Alinhamento",
            valor = BigDecimal("120.00"),
            ativo = true,
        )

    @Test
    fun `criar deve retornar servico criado`() {
        val request =
            ServicoRequest(
                descricao = "Alinhamento",
                valor = BigDecimal("120.00"),
            )

        `when`(service.criar(request))
            .thenReturn(response)

        val resultado = controller.criar(request)

        assertEquals(response, resultado)
        verify(service).criar(request)
    }

    @Test
    fun `listar ativos deve retornar servicos ativos`() {
        `when`(service.listarAtivos())
            .thenReturn(listOf(response))

        val resultado = controller.listarAtivos()

        assertEquals(1, resultado.size)
        assertEquals(id, resultado.first().id)
        assertTrue(resultado.first().ativo)

        verify(service).listarAtivos()
    }

    @Test
    fun `listar todos deve retornar ativos e inativos`() {
        val inativo =
            response.copy(
                ativo = false,
            )

        `when`(service.listarTodos())
            .thenReturn(listOf(response, inativo))

        val resultado = controller.listarTodos()

        assertEquals(2, resultado.size)
        assertTrue(resultado[0].ativo)
        assertFalse(resultado[1].ativo)

        verify(service).listarTodos()
    }

    @Test
    fun `buscar deve retornar servico pelo id`() {
        `when`(service.buscar(id))
            .thenReturn(response)

        val resultado = controller.buscar(id)

        assertEquals(response, resultado)
        verify(service).buscar(id)
    }

    @Test
    fun `atualizar deve retornar servico atualizado`() {
        val request =
            ServicoRequest(
                descricao = "Alinhamento completo",
                valor = BigDecimal("150.00"),
            )

        val atualizado =
            response.copy(
                descricao = request.descricao,
                valor = request.valor,
            )

        `when`(service.atualizar(id, request))
            .thenReturn(atualizado)

        val resultado =
            controller.atualizar(
                id = id,
                request = request,
            )

        assertEquals(atualizado, resultado)
        verify(service).atualizar(id, request)
    }

    @Test
    fun `desativar deve delegar ao service`() {
        controller.desativar(id)

        verify(service).desativar(id)
    }

    @Test
    fun `reativar deve retornar servico reativado`() {
        val reativado =
            response.copy(
                ativo = true,
            )

        `when`(service.reativar(id))
            .thenReturn(reativado)

        val resultado = controller.reativar(id)

        assertEquals(reativado, resultado)
        verify(service).reativar(id)
    }
}
