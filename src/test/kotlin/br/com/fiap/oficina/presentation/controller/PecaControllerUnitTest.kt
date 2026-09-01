package br.com.fiap.oficina.presentation.controller

import br.com.fiap.oficina.anyObject
import br.com.fiap.oficina.application.dto.PecaResponse
import br.com.fiap.oficina.application.mapper.PecaMapper
import br.com.fiap.oficina.application.service.PecaService
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import java.math.BigDecimal

class PecaControllerUnitTest {
    private val service = mock(PecaService::class.java)
    private val mapper = PecaMapper()
    private val controller = PecaController(service, mapper)

    private val pecaResponse =
        PecaResponse(
            id = "00000000-0000-0000-0000-000000000001",
            codigo = "PEC001",
            nome = "Filtro de Óleo",
            precoDeVenda = 45.00,
            qtdEstoque = 10,
            ativo = true,
        )

    private fun pecaDto() = PecaDto(
        codigo = "PEC001",
        nome = "Filtro de Óleo",
        precoDeVenda = BigDecimal("45.00"),
        qtdEstoque = 10,
    )

    @BeforeEach
    fun setupRequestContext() {
        val request = MockHttpServletRequest("POST", "/pecas")
        RequestContextHolder.setRequestAttributes(ServletRequestAttributes(request))
    }

    @AfterEach
    fun clearRequestContext() {
        RequestContextHolder.resetRequestAttributes()
    }

    @Test
    fun `criar deve retornar dto da peca salva`() {
        `when`(service.criar(anyObject())).thenReturn(pecaResponse)

        val response = controller.criar(pecaDto())

        assertEquals("PEC001", response.body?.codigo)
        assertEquals(201, response.statusCode.value())
    }

    @Test
    fun `atualizar deve retornar dto atualizado`() {
        `when`(service.atualizar(anyObject(), anyObject())).thenReturn(pecaResponse)

        val dto =
            controller.atualizar(
                "PEC001",
                PecaAtualizacaoDto(nome = "Filtro Novo", precoDeVenda = BigDecimal("60.00")),
            )

        assertEquals("PEC001", dto.codigo)
    }

    @Test
    fun `retirarPecas deve retornar dto`() {
        `when`(service.retirar("PEC001", 3)).thenReturn(pecaResponse)

        assertEquals("PEC001", controller.retirarPecas("PEC001", 3).codigo)
    }

    @Test
    fun `reporPecas deve retornar dto`() {
        `when`(service.repor("PEC001", 5)).thenReturn(pecaResponse)

        assertEquals("PEC001", controller.reporPecas("PEC001", 5).codigo)
    }

    @Test
    fun `reativar deve delegar ao service`() {
        `when`(service.reativar("PEC001")).thenReturn(true)

        assertEquals(true, controller.reativar("PEC001"))
    }

    @Test
    fun `deletar deve delegar ao service`() {
        controller.deletar("PEC001")

        verify(service).deletar("PEC001")
    }

    @Test
    fun `listar deve mapear todas as pecas`() {
        `when`(service.listar()).thenReturn(listOf(pecaResponse))

        assertEquals(1, controller.listar().size)
    }

    @Test
    fun `buscarPorCodigo deve mapear resultado`() {
        `when`(service.buscarPorCodigo("PEC001")).thenReturn(pecaResponse)

        assertEquals("PEC001", controller.buscarPorCodigo("PEC001").codigo)
    }

    @Test
    fun `buscarPorNome deve mapear resultado`() {
        `when`(service.buscarPorNome("Filtro de Óleo")).thenReturn(pecaResponse)

        assertEquals("PEC001", controller.buscarPorNome("Filtro de Óleo").codigo)
    }
}
