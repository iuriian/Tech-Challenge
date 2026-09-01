package br.com.fiap.oficina.servico.domain.entities

import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class ServicoTest {
    @Test
    fun `deve criar servico valido ativo e com id`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        assertEquals("Troca de óleo", servico.descricao)
        assertEquals(BigDecimal("150.00"), servico.valor)
        assertTrue(servico.ativo)
    }

    @Test
    fun `deve rejeitar descricao em branco`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Servico.criar(
                    descricao = "   ",
                    valor = BigDecimal("150.00"),
                )
            }

        assertEquals(
            "Descrição do serviço é obrigatória",
            exception.message,
        )
    }

    @Test
    fun `deve rejeitar valor negativo`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Servico.criar(
                    descricao = "Troca de óleo",
                    valor = BigDecimal("-0.01"),
                )
            }

        assertEquals(
            "Valor do serviço não pode ser negativo",
            exception.message,
        )
    }

    @Test
    fun `deve permitir valor zero`() {
        val servico =
            Servico.criar(
                descricao = "Diagnóstico",
                valor = BigDecimal.ZERO,
            )

        assertEquals(BigDecimal.ZERO, servico.valor)
    }

    @Test
    fun `deve alterar descricao preservando imutabilidade`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val alterado =
            servico.alterarDescricao("Troca de óleo e filtro")

        assertEquals("Troca de óleo", servico.descricao)
        assertEquals("Troca de óleo e filtro", alterado.descricao)
        assertEquals(servico.id, alterado.id)
        assertEquals(servico.valor, alterado.valor)
    }

    @Test
    fun `deve rejeitar nova descricao invalida`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                servico.alterarDescricao("")
            }

        assertEquals(
            "Descrição do serviço é obrigatória",
            exception.message,
        )
    }

    @Test
    fun `deve alterar valor preservando imutabilidade`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val alterado =
            servico.alterarValor(BigDecimal("180.00"))

        assertEquals(BigDecimal("150.00"), servico.valor)
        assertEquals(BigDecimal("180.00"), alterado.valor)
        assertEquals(servico.id, alterado.id)
        assertEquals(servico.descricao, alterado.descricao)
    }

    @Test
    fun `deve rejeitar novo valor negativo`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                servico.alterarValor(BigDecimal("-1.00"))
            }

        assertEquals(
            "Valor do serviço não pode ser negativo",
            exception.message,
        )
    }

    @Test
    fun `deve desativar preservando imutabilidade`() {
        val servico =
            Servico.criar(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val desativado = servico.desativar()

        assertTrue(servico.ativo)
        assertFalse(desativado.ativo)
        assertEquals(servico.id, desativado.id)
    }

    @Test
    fun `deve reativar preservando imutabilidade`() {
        val servico =
            Servico(
                id = Id.generate(),
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
                ativo = false,
            )

        val reativado = servico.reativar()

        assertFalse(servico.ativo)
        assertTrue(reativado.ativo)
        assertEquals(servico.id, reativado.id)
    }
}
