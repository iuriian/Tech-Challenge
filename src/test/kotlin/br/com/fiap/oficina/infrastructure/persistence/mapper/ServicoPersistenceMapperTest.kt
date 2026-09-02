package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.entity.ServicoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoPersistenceMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class ServicoPersistenceMapperTest {
    private val mapper = ServicoPersistenceMapper()

    @Test
    fun `deve converter entidade jpa para dominio`() {
        val id = UUID.randomUUID()

        val entity =
            ServicoJpaEntity(
                id = id,
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
                ativo = false,
            )

        val resultado = mapper.toDomain(entity)

        assertEquals(Id(id), resultado.id)
        assertEquals("Troca de óleo", resultado.descricao)
        assertEquals(BigDecimal("150.00"), resultado.valor)
        assertFalse(resultado.ativo)
    }

    @Test
    fun `deve converter dominio para entidade jpa`() {
        val servico =
            Servico(
                id = Id.generate(),
                descricao = "Alinhamento",
                valor = BigDecimal("100.00"),
                ativo = false,
            )

        val resultado = mapper.toJpaEntity(servico)

        assertEquals(servico.id.valor, resultado.id)
        assertEquals(servico.descricao, resultado.descricao)
        assertEquals(servico.valor, resultado.valor)
        assertEquals(servico.ativo, resultado.ativo)
    }
}
