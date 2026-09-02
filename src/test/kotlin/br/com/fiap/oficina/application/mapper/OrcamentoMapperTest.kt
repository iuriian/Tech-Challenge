package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Orcamento
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class OrcamentoMapperTest {
    private val mapper =
        OrcamentoMapper(
            ItemOrcamentoMapper(),
        )

    @Test
    fun `deve mapear orcamento para response`() {
        val referenciaId = Id.generate()

        val orcamento =
            Orcamento(
                itens =
                listOf(
                    ItemOrcamento(
                        tipo = TipoItemOrcamento.PECA,
                        referenciaId = referenciaId,
                        descricao = "Filtro",
                        valorUnitario = BigDecimal.TEN,
                        quantidade = BigDecimal("2"),
                        codigoReferencia = "PEC001",
                    ),
                ),
            )

        val response = mapper.toResponse(orcamento)

        assertEquals(BigDecimal("20"), response.valorTotal)
        assertEquals(1, response.itens.size)

        val item = response.itens.single()

        assertEquals(TipoItemOrcamento.PECA, item.tipo)
        assertEquals(referenciaId.valor, item.referenciaId)
        assertEquals("PEC001", item.codigoReferencia)
        assertEquals("Filtro", item.descricao)
        assertEquals(BigDecimal.TEN, item.valorUnitario)
        assertEquals(BigDecimal("2"), item.quantidade)
        assertEquals(BigDecimal("20"), item.subtotal)
    }
}
