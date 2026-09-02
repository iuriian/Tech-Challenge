package br.com.fiap.oficina.domain.valueobject

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.Servico
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrcamentoTest {
    @Test
    fun `deve iniciar vazio com valor total zero`() {
        val orcamento = Orcamento()

        assertTrue(orcamento.itens.isEmpty())
        assertEquals(BigDecimal.ZERO, orcamento.valorTotal)
    }

    @Test
    fun `deve calcular valor total a partir dos itens`() {
        val peca =
            Peca(
                id = Id.generate(),
                codigo = "PEC001",
                nome = "Filtro",
                precoDeVenda = BigDecimal.TEN,
            )

        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val itemPeca =
            ItemOrcamento.dePeca(
                peca = peca,
                quantidade = BigDecimal("2"),
            )

        val itemServico =
            ItemOrcamento.deServico(servico)

        val orcamento =
            Orcamento(
                itens = listOf(
                    itemPeca,
                    itemServico,
                ),
            )

        assertEquals(
            0,
            BigDecimal("170.00").compareTo(orcamento.valorTotal),
        )
    }

    @Test
    fun `deve adicionar item preservando orcamento original`() {
        val servico =
            Servico.criar(
                descricao = "Alinhamento",
                valor = BigDecimal("120.00"),
            )

        val item = ItemOrcamento.deServico(servico)

        val original = Orcamento()
        val atualizado = original.adicionarItem(item)

        assertTrue(original.itens.isEmpty())
        assertEquals(listOf(item), atualizado.itens)
        assertEquals(
            0,
            BigDecimal("120.00").compareTo(atualizado.valorTotal),
        )
    }
}
