package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PecaServicoTest {

    private val peca = Peca(
        id = Id.gerar(),
        codigo = "PEC001",
        nome = "Filtro",
        precoDeVenda = BigDecimal.TEN
    )

    @Test
    fun `deve criar peca-servico com quantidade valida`() {
        val pecaServico = PecaServico.criar(peca, BigDecimal("2.5"))

        assertEquals(peca, pecaServico.peca)
        assertEquals(BigDecimal("2.5"), pecaServico.quantidade)
    }

    @Test
    fun `deve rejeitar quantidade zero`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            PecaServico.criar(peca, BigDecimal.ZERO)
        }

        assertEquals("Quantidade consumida da peça deve ser maior que zero", exception.message)
    }

    @Test
    fun `deve rejeitar quantidade negativa`() {
        assertThrows(IllegalArgumentException::class.java) {
            PecaServico.criar(peca, BigDecimal("-1"))
        }
    }

    @Test
    fun `subtotal deve multiplicar preco de venda pela quantidade`() {
        val pecaServico = PecaServico.criar(peca, BigDecimal("2.5"))

        assertEquals(BigDecimal("25.0"), pecaServico.subtotal())
    }
}
