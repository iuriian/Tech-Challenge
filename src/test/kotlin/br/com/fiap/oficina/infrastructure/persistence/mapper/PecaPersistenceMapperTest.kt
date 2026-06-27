package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class PecaPersistenceMapperTest {
    private val mapper = PecaPersistenceMapper()

    @Test
    fun `deve fazer round-trip de peca completa`() {
        val peca =
            Peca(
                id = Id.generate(),
                codigo = "PEC001",
                nome = "Filtro de Óleo",
                descricao = "Filtro padrão",
                fabricante = "Bosch",
                fornecedor = "AutoParts",
                precoDeCompra = BigDecimal("25.00"),
                precoDeVenda = BigDecimal("45.00"),
                qtdEstoque = 50,
                ativo = true,
            )

        val jpa = mapper.toJpa(peca)

        assertEquals(peca.id.valor, jpa.id)
        assertEquals(peca.codigo, jpa.codigo)
        assertEquals(peca.precoDeVenda, jpa.precoDeVenda)

        assertEquals(peca, mapper.toDomain(jpa))
    }

    @Test
    fun `deve fazer round-trip de peca com campos opcionais nulos`() {
        val peca =
            Peca(
                id = Id.generate(),
                codigo = "PEC002",
                nome = "Vela",
                precoDeVenda = BigDecimal("32.00"),
                qtdEstoque = 10,
                ativo = false,
            )

        assertEquals(peca, mapper.toDomain(mapper.toJpa(peca)))
    }
}
