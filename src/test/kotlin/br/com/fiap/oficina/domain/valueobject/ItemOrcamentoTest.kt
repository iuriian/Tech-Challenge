package br.com.fiap.oficina.domain.valueobject

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ItemOrcamentoTest {
    private val peca =
        Peca(
            id = Id.generate(),
            codigo = "PEC001",
            nome = "Filtro",
            precoDeVenda = BigDecimal.TEN,
        )

    @Test
    fun `deve criar item de orcamento a partir de peca`() {
        val item =
            ItemOrcamento.dePeca(
                peca = peca,
                quantidade = BigDecimal("2.5"),
            )

        assertEquals(TipoItemOrcamento.PECA, item.tipo)
        assertEquals(peca.id, item.referenciaId)
        assertEquals("Filtro", item.descricao)
        assertEquals(BigDecimal.TEN, item.valorUnitario)
        assertEquals(BigDecimal("2.5"), item.quantidade)
        assertEquals("PEC001", item.codigoReferencia)
        assertEquals(BigDecimal("25.0"), item.subtotal)
    }

    @Test
    fun `deve criar item de orcamento a partir de servico`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val item = ItemOrcamento.deServico(servico)

        assertEquals(TipoItemOrcamento.SERVICO, item.tipo)
        assertEquals(servico.id, item.referenciaId)
        assertEquals("Troca de óleo", item.descricao)
        assertEquals(BigDecimal("150.00"), item.valorUnitario)
        assertEquals(BigDecimal.ONE, item.quantidade)
        assertNull(item.codigoReferencia)
        assertEquals(BigDecimal("150.00"), item.subtotal)
    }

    @Test
    fun `deve rejeitar quantidade zero`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                ItemOrcamento.dePeca(
                    peca = peca,
                    quantidade = BigDecimal.ZERO,
                )
            }

        assertEquals(
            "Quantidade do item deve ser maior que zero",
            exception.message,
        )
    }

    @Test
    fun `deve rejeitar quantidade negativa`() {
        assertThrows(IllegalArgumentException::class.java) {
            ItemOrcamento.dePeca(
                peca = peca,
                quantidade = BigDecimal("-1"),
            )
        }
    }
}
