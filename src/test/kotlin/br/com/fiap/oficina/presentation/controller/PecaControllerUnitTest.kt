package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.service.PecaService
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import br.com.fiap.oficina.presentation.mapper.PecaMapper
import org.junit.Assert.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PecaControllerUnitTest {
    private val service = mock(PecaService::class.java)
    private val controller = PecaController(service, PecaMapper())

    private val peca =
        Peca(
            id = Id.generate(),
            codigo = "PEC001",
            nome = "Filtro de Óleo",
            precoDeVenda = BigDecimal("45.00"),
            qtdEstoque = 10,
        )

    private fun pecaDto() = PecaDto(
        codigo = "PEC001",
        nome = "Filtro de Óleo",
        precoDeVenda = BigDecimal("45.00"),
        qtdEstoque = 10,
    )

    @Test
    fun `criar deve retornar dto da peca salva`() {
        `when`(service.salvarPeca(anyObject())).thenReturn(peca)

        val dto = controller.criar(pecaDto())

        assertEquals("PEC001", dto.codigo)
        assertEquals(peca.id.valor, dto.id)
    }

    @Test
    fun `criar deve traduzir conflito em 409`() {
        `when`(service.salvarPeca(anyObject())).thenThrow(IllegalArgumentException("Peça já cadastrada"))

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                controller.criar(pecaDto())
            }

        assertEquals(HttpStatus.CONFLICT, exception.statusCode)
    }

    @Test
    fun `atualizar deve retornar dto atualizado`() {
        `when`(service.atualizarPeca(anyObject(), anyObject())).thenReturn(peca)

        val dto =
            controller.atualizar(
                "PEC001",
                PecaAtualizacaoDto(nome = "Filtro Novo", precoDeVenda = BigDecimal("60.00")),
            )

        assertEquals("PEC001", dto?.codigo)
    }

    @Test
    fun `retirarPecas deve retornar dto`() {
        `when`(service.retirarPecas("PEC001", 3)).thenReturn(peca)

        assertNotNull(controller.retirarPecas("PEC001", 3))
    }

    @Test
    fun `retirarPecas deve traduzir erro em 400`() {
        `when`(service.retirarPecas("PEC001", 99))
            .thenThrow(IllegalArgumentException("Quantidade em estoque insuficiente"))

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                controller.retirarPecas("PEC001", 99)
            }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun `reporPecas deve retornar dto`() {
        `when`(service.reporPecas("PEC001", 5)).thenReturn(peca)

        assertNotNull(controller.reporPecas("PEC001", 5))
    }

    @Test
    fun `reporPecas deve traduzir erro em 400`() {
        `when`(service.reporPecas("PEC001", -1))
            .thenThrow(IllegalArgumentException("Quantidade para reposição deve ser maior que zero"))

        val exception =
            assertThrows(ResponseStatusException::class.java) {
                controller.reporPecas("PEC001", -1)
            }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
    }

    @Test
    fun `reativar deve delegar ao service`() {
        `when`(service.reativarPeca("PEC001")).thenReturn(true)

        assertTrue(controller.reativar("PEC001"))
    }

    @Test
    fun `deletar deve delegar ao service`() {
        `when`(service.deletarPeca("PEC001")).thenReturn(true)

        assertTrue(controller.deletar("PEC001"))
    }

    @Test
    fun `listar deve mapear todas as pecas`() {
        `when`(service.listarPecas()).thenReturn(listOf(peca))

        assertEquals(1, controller.listar().size)
    }

    @Test
    fun `buscarPorCodigo deve mapear resultado`() {
        `when`(service.buscarPorCodigo("PEC001")).thenReturn(peca)

        assertEquals("PEC001", controller.buscarPorCodigo("PEC001")?.codigo)
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(service.buscarPorNome("Filtro de Óleo")).thenReturn(peca)

        assertEquals("PEC001", controller.buscarPorNome("Filtro de Óleo")?.codigo)
    }
}
