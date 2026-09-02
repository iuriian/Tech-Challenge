package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class ItemOrcamentoMapperTest {
    private val mapper = ItemOrcamentoMapper()

    @Test
    fun `deve mapear item de orcamento para response`() {
        val referenciaId = Id.generate()

        val item =
            ItemOrcamento(
                tipo = TipoItemOrcamento.PECA,
                referenciaId = referenciaId,
                descricao = "Filtro",
                valorUnitario = BigDecimal.TEN,
                quantidade = BigDecimal("2"),
                codigoReferencia = "PEC001",
            )

        val response = mapper.toResponse(item)

        assertEquals(TipoItemOrcamento.PECA, response.tipo)
        assertEquals(referenciaId.valor, response.referenciaId)
        assertEquals("PEC001", response.codigoReferencia)
        assertEquals("Filtro", response.descricao)
        assertEquals(BigDecimal.TEN, response.valorUnitario)
        assertEquals(BigDecimal("2"), response.quantidade)
        assertEquals(BigDecimal("20"), response.subtotal)
    }
}
