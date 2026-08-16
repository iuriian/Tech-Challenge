package br.com.fiap.oficina.domain.valueobject

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.enum.TipoItemOrcamento
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertEquals
import java.math.BigDecimal



class OrcamentoTest {

    private val peca =
        Peca(
            id = Id.generate(),
            codigo = "PEC001",
            nome = "Filtro",
            precoDeVenda = BigDecimal.TEN,
        )

    private val servico =
        Servico(
            id = Id.generate(),
            descricao = "Troca de óleo",
            valor = BigDecimal("150.00"),
        )

    @Test
    fun `deve criar item de peca como snapshot`() {
        val item = ItemOrcamento.dePeca(peca, BigDecimal("2"))

        assertEquals(TipoItemOrcamento.PECA, item.tipo)
        assertEquals(peca.id, item.referenciaId)
        assertEquals("PEC001", item.codigoReferencia)
        assertEquals("Filtro", item.descricao)
        assertEquals(BigDecimal.TEN, item.valorUnitario)
        assertEquals(BigDecimal("2"), item.quantidade)
        assertEquals(BigDecimal("30.0"), item.subtotal)
    }

    @Test
    fun `deve criar item de servico como snapshot`() {
        val item = ItemOrcamento.deServico(servico)

        assertEquals(TipoItemOrcamento.SERVICO, item.tipo)
        assertEquals(servico.id, item.referenciaId)
        assertEquals("Troca de óleo", item.descricao)
        assertEquals(BigDecimal("150.00"), item.valorUnitario)
        assertEquals(BigDecimal.ONE, item.quantidade)
    }

    @Test
    fun `deve calcular total com pecas e servicos`() {
        val orcamento =
            Orcamento(
                ordemServicoId = Id.generate(),
                itens =
                    listOf(
                        ItemOrcamento.dePeca(peca, BigDecimal("2")),
                        ItemOrcamento.deServico(servico),
                    ),
            )
        assertEquals(0, BigDecimal("170.0").compareTo(orcamento.valorTotal))
    }

    @Test
    fun `deve impedir quantidade igual a zero`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                ItemOrcamento.dePeca(peca, BigDecimal.ZERO)
            }

        assertEquals("Quantidade do item deve ser maior que zero", exception.message)
    }

    @Test
    fun `deve impedir valor unitario negativo`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                ItemOrcamento(
                    tipo = TipoItemOrcamento.SERVICO,
                    referenciaId = Id.generate(),
                    descricao = "Serviço inválido",
                    valorUnitario = BigDecimal("-10.00"),
                    quantidade = BigDecimal.ONE,
                )
            }
        assertEquals("Valor unitário do item não pode ser negativo", exception.message)
    }

    @Test
    @Suppress("DEPRECATION")
    fun `deve rejeitar total legado inconsistente`() {
        val item = ItemOrcamento.dePeca(peca, BigDecimal("2"))

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Orcamento(
                    servicoId = Id.generate(),
                    itens = listOf(item),
                    valorTotal = BigDecimal("999.00"),
                )
            }

        assertEquals("Valor total informado é incompatível com os itens do orçamento", exception.message)
    }

}