package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.presentation.dto.PecaAtualizacaoDto
import br.com.fiap.oficina.presentation.dto.PecaDto
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PecaMapperTest {
    private val mapper = PecaMapper()

    @Test
    fun `deve mapear Peca para PecaDto`() {
        val peca =
            Peca(
                id = Id.generate(),
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                descricao = "Filtro padrão",
                fabricante = "Bosch",
                fornecedor = "AutoParts",
                precoDeCompra = BigDecimal("25.00"),
                precoDeVenda = BigDecimal("45.00"),
                qtdEstoque = 50,
                ativo = true,
            )

        val dto = mapper.toDto(peca)

        assertEquals(peca.id.valor, dto.id)
        assertEquals("PEC001", dto.codigo)
        assertEquals("Filtro de Óleo", dto.nome)
        assertEquals(BigDecimal("45.00"), dto.precoDeVenda)
        assertEquals(50, dto.qtdEstoque)
        assertTrue(dto.ativo)
    }

    @Test
    fun `deve mapear PecaDto para Peca`() {
        val dto =
            PecaDto(
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                descricao = "Filtro padrão",
                precoDeVenda = BigDecimal("45.00"),
                qtdEstoque = 50,
            )

        val peca = mapper.toEntity(dto)

        assertNotNull(peca.id)
        assertEquals("PEC001", peca.codigo)
        assertEquals("Filtro de Óleo", peca.nome)
        assertEquals(50, peca.qtdEstoque)
    }

    @Test
    fun `deve mapear PecaAtualizacaoDto usando codigo temporario e estoque zero`() {
        val dto =
            PecaAtualizacaoDto(
                nome = "Filtro de Ar",
                descricao = "Novo filtro",
                precoDeVenda = BigDecimal("65.00"),
            )

        val peca = mapper.toEntity(dto)

        assertEquals("TMP", peca.codigo)
        assertEquals("Filtro de Ar", peca.nome)
        assertEquals(0, peca.qtdEstoque)
        assertEquals(BigDecimal("65.00"), peca.precoDeVenda)
    }
}
