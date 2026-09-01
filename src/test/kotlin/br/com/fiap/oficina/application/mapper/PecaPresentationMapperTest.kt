package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.PecaResponse
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PecaPresentationMapperTest {
    private val mapper = PecaMapper()

    private val pecaResponse =
        PecaResponse(
            id = "00000000-0000-0000-0000-000000000001",
            codigo = "PEC001",
            nome = "Filtro de Óleo",
            descricao = "Filtro padrão",
            fabricante = "Bosch",
            fornecedor = "AutoParts",
            precoDeCompra = 25.00,
            precoDeVenda = 45.00,
            qtdEstoque = 50,
            ativo = true,
        )

    @Test
    fun `deve mapear PecaResponse para PecaDto de apresentacao`() {
        val dto = mapper.toDto(pecaResponse)

        assertEquals(UUID.fromString(pecaResponse.id), dto.id)
        assertEquals("PEC001", dto.codigo)
        assertEquals("Filtro de Óleo", dto.nome)
        assertEquals(0, BigDecimal("45.00").compareTo(dto.precoDeVenda))
        assertEquals(50, dto.qtdEstoque)
        assertEquals(true, dto.ativo)
    }

    @Test
    fun `deve mapear PecaDto de apresentacao para PecaRequest`() {
        val dto =
            PecaDto(
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                descricao = "Filtro padrão",
                precoDeVenda = BigDecimal("45.00"),
                qtdEstoque = 50,
            )

        val request = mapper.toCriarRequest(dto)

        assertEquals("PEC001", request.codigo)
        assertEquals("Filtro de Óleo", request.nome)
        assertEquals(50, request.qtdEstoque)
        assertEquals(45.00, request.precoDeVenda)
    }

    @Test
    fun `deve mapear PecaAtualizacaoDto para PecaRequest sem codigo`() {
        val dto =
            PecaAtualizacaoDto(
                nome = "Filtro de Ar",
                descricao = "Novo filtro",
                precoDeVenda = BigDecimal("65.00"),
            )

        val request = mapper.toPecaRequest(dto)

        assertNull(request.codigo)
        assertEquals("Filtro de Ar", request.nome)
        assertEquals(65.00, request.precoDeVenda)
    }
}
