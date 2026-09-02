package br.com.fiap.oficina.domain.valueobject

import br.com.fiap.oficina.domain.valueobject.NumeroOrdemServico
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class NumeroOrdemServicoTest {
    @Test
    fun `deve criar numero de ordem de servico formatado`() {
        val numero =
            NumeroOrdemServico.criar(
                sequencial = 123,
                ano = 2026,
            )

        assertEquals(
            "OS-2026-000123",
            numero.valor,
        )
    }

    @Test
    fun `deve preencher sequencial com zeros a esquerda`() {
        val numero =
            NumeroOrdemServico.criar(
                sequencial = 1,
                ano = 2026,
            )

        assertEquals(
            "OS-2026-000001",
            numero.valor,
        )
    }

    @Test
    fun `deve rejeitar sequencial zero`() {
        assertThrows(IllegalArgumentException::class.java) {
            NumeroOrdemServico.criar(
                sequencial = 0,
                ano = 2026,
            )
        }
    }

    @Test
    fun `deve rejeitar sequencial negativo`() {
        assertThrows(IllegalArgumentException::class.java) {
            NumeroOrdemServico.criar(
                sequencial = -1,
                ano = 2026,
            )
        }
    }

    @Test
    fun `deve rejeitar numero em branco`() {
        assertThrows(IllegalArgumentException::class.java) {
            NumeroOrdemServico("")
        }
    }

    @Test
    fun `deve rejeitar numero maior que cinquenta caracteres`() {
        assertThrows(IllegalArgumentException::class.java) {
            NumeroOrdemServico(
                "A".repeat(51),
            )
        }
    }

    @Test
    fun `toString deve retornar valor do numero`() {
        val numero =
            NumeroOrdemServico(
                "OS-2026-000001",
            )

        assertEquals(
            "OS-2026-000001",
            numero.toString(),
        )
    }
}
