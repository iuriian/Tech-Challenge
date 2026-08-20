package br.com.fiap.oficina.infrastructure.persistence.entity

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PecaJpaEntityTest {
    @Test
    fun `deve aplicar valores padrao no construtor sem argumentos`() {
        val entity = PecaJpaEntity()

        assertNotNull(entity.id)
        assertEquals("", entity.codigo)
        assertEquals("", entity.nome)
        assertNull(entity.descricao)
        assertNull(entity.fabricante)
        assertNull(entity.fornecedor)
        assertNull(entity.precoDeCompra)
        assertEquals(BigDecimal.ZERO, entity.precoDeVenda)
        assertEquals(0, entity.qtdEstoque)
        assertTrue(entity.ativo)
    }

    @Test
    fun `deve permitir construir com parte dos argumentos e mutar propriedades`() {
        val entity =
            PecaJpaEntity(
                codigo = "PEC002",
                nome = "Vela",
                precoDeVenda = BigDecimal("32.00"),
            )

        assertEquals("PEC002", entity.codigo)
        assertEquals(BigDecimal("32.00"), entity.precoDeVenda)

        val novoId = UUID.randomUUID()
        entity.id = novoId
        entity.descricao = "Vela de ignição"
        entity.fabricante = "NGK"
        entity.fornecedor = "Peças Express"
        entity.precoDeCompra = BigDecimal("18.00")
        entity.qtdEstoque = 80
        entity.ativo = false

        assertEquals(novoId, entity.id)
        assertEquals("Vela de ignição", entity.descricao)
        assertEquals("NGK", entity.fabricante)
        assertEquals("Peças Express", entity.fornecedor)
        assertEquals(BigDecimal("18.00"), entity.precoDeCompra)
        assertEquals(80, entity.qtdEstoque)
        assertFalse(entity.ativo)
    }
}
