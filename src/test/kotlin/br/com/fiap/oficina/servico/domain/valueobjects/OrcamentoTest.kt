package br.com.fiap.oficina.servico.domain.valueobjects

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.enums.TipoItemOrcamento
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class OrcamentoTest {
    private val peca =
        Peca(
            id = Id.generate(),
            codigo = "PEC001",
            nome = "Filtro",
            precoDeVenda = BigDecimal.TEN,
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
        assertEquals(0, BigDecimal("20").compareTo(item.subtotal))
    }

    @Test
    fun `deve permitir item generico de servico`() {
        val servicoId = Id.generate()

        val item =
            ItemOrcamento(
                tipo = TipoItemOrcamento.SERVICO,
                referenciaId = servicoId,
                descricao = "Troca de óleo",
                valorUnitario = BigDecimal("150.00"),
                quantidade = BigDecimal.ONE,
            )

        assertEquals(TipoItemOrcamento.SERVICO, item.tipo)
        assertEquals(servicoId, item.referenciaId)
        assertEquals("Troca de óleo", item.descricao)
        assertEquals(BigDecimal("150.00"), item.valorUnitario)
        assertEquals(BigDecimal.ONE, item.quantidade)
        assertEquals(0, BigDecimal("150.00").compareTo(item.subtotal))
    }

    @Test
    fun `deve calcular total a partir dos itens`() {
        val itemPeca = ItemOrcamento.dePeca(peca, BigDecimal("2"))
        val itemServico =
            ItemOrcamento(
                tipo = TipoItemOrcamento.SERVICO,
                referenciaId = Id.generate(),
                descricao = "Troca de óleo",
                valorUnitario = BigDecimal("150.00"),
                quantidade = BigDecimal.ONE,
            )

        val orcamento =
            Orcamento(
                ordemServicoId = Id.generate(),
                itens = listOf(itemPeca, itemServico),
            )

        assertEquals(0, BigDecimal("170.00").compareTo(orcamento.valorTotal))
    }

    @Test
    fun `deve impedir descricao em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                ItemOrcamento(
                    tipo = TipoItemOrcamento.SERVICO,
                    referenciaId = Id.generate(),
                    descricao = "",
                    valorUnitario = BigDecimal.TEN,
                    quantidade = BigDecimal.ONE,
                )
            }

        assertEquals("Descrição do item do orçamento é obrigatória", exception.message)
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
    fun `deve impedir quantidade igual a zero`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                ItemOrcamento.dePeca(peca, BigDecimal.ZERO)
            }

        assertEquals("Quantidade do item deve ser maior que zero", exception.message)
    }

    @Test
    fun `deve exigir codigo para item de peca`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                ItemOrcamento(
                    tipo = TipoItemOrcamento.PECA,
                    referenciaId = Id.generate(),
                    descricao = "Filtro",
                    valorUnitario = BigDecimal.TEN,
                    quantidade = BigDecimal.ONE,
                )
            }

        assertEquals("Código da peça é obrigatório", exception.message)
    }
}
