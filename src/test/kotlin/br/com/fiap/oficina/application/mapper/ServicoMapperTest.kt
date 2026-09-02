package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.ServicoRequest
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class ServicoMapperTest {
    private val mapper = ServicoMapper()

    @Test
    fun `deve converter request para novo servico`() {
        val request =
            ServicoRequest(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val resultado = mapper.toDomain(request)

        assertEquals("Troca de óleo", resultado.descricao)
        assertEquals(BigDecimal("150.00"), resultado.valor)
        assertTrue(resultado.ativo)
    }

    @Test
    fun `deve converter request e id para servico existente`() {
        val id = UUID.randomUUID()
        val request =
            ServicoRequest(
                descricao = "Troca de óleo completa",
                valor = BigDecimal("180.00"),
            )

        val resultado =
            mapper.toDomain(
                id = Id(id),
                request = request,
            )

        assertEquals(id, resultado.id.valor)
        assertEquals("Troca de óleo completa", resultado.descricao)
        assertEquals(BigDecimal("180.00"), resultado.valor)
    }

    @Test
    fun `deve converter servico para response`() {
        val id = UUID.randomUUID()
        val servico =
            Servico(
                id = Id(id),
                descricao = "Alinhamento",
                valor = BigDecimal("120.00"),
                ativo = false,
            )

        val resultado = mapper.toResponse(servico)

        assertEquals(id, resultado.id)
        assertEquals("Alinhamento", resultado.descricao)
        assertEquals(BigDecimal("120.00"), resultado.valor)
        assertFalse(resultado.ativo)
    }
}
