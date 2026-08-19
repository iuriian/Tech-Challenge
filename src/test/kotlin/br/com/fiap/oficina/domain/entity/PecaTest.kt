package br.com.fiap.oficina.domain.entity

import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PecaTest {
    @Test
    fun `deve criar peca com dados validos`() {
        val peca =
            Peca.criar(
                codigo = "PEC010",
                nome = "Filtro de Combustível",
                descricao = "Filtro de combustível",
                fabricante = "Tecfil",
                fornecedor = "AutoParts Ltda",
                precoDeCompra = BigDecimal("22.50"),
                precoDeVenda = BigDecimal("45.90"),
                qtdEstoque = 15,
            )

        assertEquals("PEC010", peca.codigo)
        assertEquals("Filtro de Combustível", peca.nome)
        assertEquals(BigDecimal("45.90"), peca.precoDeVenda)
        assertEquals(15, peca.qtdEstoque)
        assertTrue(peca.ativo)
    }

    @Test
    fun `deve controlar status ativo da peca`() {
        val peca = criarPeca()

        val pecaDesativada = peca.desativar()
        assertFalse(pecaDesativada.ativo)

        val pecaReativada = pecaDesativada.reativar()
        assertTrue(pecaReativada.ativo)
    }

    @Test
    fun `deve retirar e repor estoque`() {
        val peca = criarPeca(qtdEstoque = 10)

        val pecaComRetirada = peca.retirarPecas(3)
        assertEquals(7, pecaComRetirada.qtdEstoque)

        val pecaComReposicao = pecaComRetirada.reporPecas(5)
        assertEquals(12, pecaComReposicao.qtdEstoque)
    }

    @Test
    fun `deve impedir estoque negativo na criacao`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Peca.criar(
                    codigo = "PEC011",
                    nome = "Sensor MAP",
                    precoDeVenda = BigDecimal("180.00"),
                    qtdEstoque = -1,
                )
            }

        assertEquals("Quantidade em estoque não pode ser negativa", exception.message)
    }

    @Test
    fun `deve impedir retirada maior que estoque`() {
        val peca = criarPeca(qtdEstoque = 2)

        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                peca.retirarPecas(3)
            }

        assertEquals("Quantidade em estoque insuficiente", exception.message)
    }

    @Test
    fun `deve impedir codigo em branco na criacao`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Peca.criar(codigo = "", nome = "Sensor", precoDeVenda = BigDecimal("10.00"), qtdEstoque = 1)
            }
        assertEquals("Código da peça é obrigatório", exception.message)
    }

    @Test
    fun `deve impedir nome em branco na criacao`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Peca.criar(codigo = "PEC012", nome = "", precoDeVenda = BigDecimal("10.00"), qtdEstoque = 1)
            }
        assertEquals("Nome da peça é obrigatório", exception.message)
    }

    @Test
    fun `deve impedir preco de compra negativo na criacao`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Peca.criar(
                    codigo = "PEC013",
                    nome = "Sensor",
                    precoDeCompra = BigDecimal("-1.00"),
                    precoDeVenda = BigDecimal("10.00"),
                    qtdEstoque = 1,
                )
            }
        assertEquals("Preço de compra não pode ser negativo", exception.message)
    }

    @Test
    fun `deve impedir preco de venda negativo na criacao`() {
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                Peca.criar(
                    codigo = "PEC014",
                    nome = "Sensor",
                    precoDeVenda = BigDecimal("-10.00"),
                    qtdEstoque = 1,
                )
            }
        assertEquals("Preço de venda não pode ser negativo", exception.message)
    }

    @Test
    fun `deve impedir retirada e reposicao com quantidade invalida`() {
        val peca = criarPeca(qtdEstoque = 5)

        val retirada = assertThrows(IllegalArgumentException::class.java) { peca.retirarPecas(0) }
        assertEquals("Quantidade para retirada deve ser maior que zero", retirada.message)

        val reposicao = assertThrows(IllegalArgumentException::class.java) { peca.reporPecas(0) }
        assertEquals("Quantidade para reposição deve ser maior que zero", reposicao.message)
    }

    private fun criarPeca(qtdEstoque: Int = 0): Peca =
        Peca(
            id = Id.fromString("00000000-0000-0000-0000-000000000010"),
            codigo = "PEC010",
            nome = "Filtro de Combustível",
            precoDeVenda = BigDecimal("45.90"),
            qtdEstoque = qtdEstoque,
        )
}
