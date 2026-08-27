package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.AtualizarPecaRequest
import br.com.fiap.oficina.application.dto.CriarPecaRequest
import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PecaApplicationMapperTest {
    private val mapper = PecaApplicationMapper()

    @Test
    fun `deve mapear CriarPecaRequest para dominio`() {
        val request =
            CriarPecaRequest(
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                descricao = "Filtro padrão",
                fabricante = "Bosch",
                fornecedor = "AutoParts",
                precoDeCompra = BigDecimal("25.00"),
                precoDeVenda = BigDecimal("45.00"),
                qtdEstoque = 50,
            )

        val peca = mapper.toDomain(request)

        assertEquals("PEC001", peca.codigo)
        assertEquals("Filtro de Óleo", peca.nome)
        assertEquals(50, peca.qtdEstoque)
    }

    @Test
    fun `deve mapear AtualizarPecaRequest para dominio com estoque zerado`() {
        val request =
            AtualizarPecaRequest(
                nome = "Filtro de Ar",
                descricao = "Novo filtro",
                fabricante = "Mann",
                fornecedor = "PartsCo",
                precoDeCompra = BigDecimal("30.00"),
                precoDeVenda = BigDecimal("65.00"),
            )

        val peca = mapper.toDomain("PEC002", request)

        assertEquals("PEC002", peca.codigo)
        assertEquals("Filtro de Ar", peca.nome)
        assertEquals(0, peca.qtdEstoque)
    }

    @Test
    fun `deve mapear Peca para PecaResponse`() {
        val peca =
            Peca(
                id = Id.fromString("00000000-0000-0000-0000-000000000001"),
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                precoDeVenda = BigDecimal("45.00"),
                qtdEstoque = 10,
            )

        val response = mapper.toResponse(peca)

        assertEquals("00000000-0000-0000-0000-000000000001", response.id)
        assertEquals("PEC001", response.codigo)
        assertEquals(10, response.qtdEstoque)
        assertEquals(true, response.ativo)
    }
}
