package br.com.fiap.oficina.domain.entity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ServicoTest {
    @Test
    fun `deve criar servico de catalogo com dados validos`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        assertEquals("Troca de óleo", servico.descricao)
        assertEquals(BigDecimal("150.00"), servico.valor)
    }

    @Test
    fun `deve impedir descricao em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Servico.criar(
                    descricao = "",
                    valor = BigDecimal("150.00"),
                )
            }

        assertEquals("Descrição do serviço é obrigatória", exception.message)
    }

    @Test
    fun `deve impedir valor negativo`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Servico.criar(
                    descricao = "Troca de óleo",
                    valor = BigDecimal("-0.01"),
                )
            }

        assertEquals("Valor do serviço não pode ser negativo", exception.message)
    }

    @Test
    fun `deve permitir servico com valor zero`() {
        val servico =
            Servico.criar(
                descricao = "Diagnóstico gratuito",
                valor = BigDecimal.ZERO,
            )

        assertEquals(BigDecimal.ZERO, servico.valor)
    }

    @Test
    fun `deve alterar descricao preservando imutabilidade`() {
        val original =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val alterado = original.alterarDescricao("Troca de óleo e filtro")

        assertEquals("Troca de óleo", original.descricao)
        assertEquals("Troca de óleo e filtro", alterado.descricao)
    }

    @Test
    fun `deve alterar valor preservando imutabilidade`() {
        val original =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val alterado = original.alterarValor(BigDecimal("175.00"))

        assertEquals(BigDecimal("150.00"), original.valor)
        assertEquals(BigDecimal("175.00"), alterado.valor)
    }

    @Test
    fun `deve validar alteracoes realizadas por copy`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        assertThrows(IllegalArgumentException::class.java) {
            servico.copy(valor = BigDecimal("-1.00"))
        }
    }
}