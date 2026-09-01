package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.AtualizarServicoRequest
import br.com.fiap.oficina.application.dto.CriarServicoRequest
import br.com.fiap.oficina.domain.usecase.servico.AtualizarServicoInput
import br.com.fiap.oficina.domain.usecase.servico.CriarServicoInput
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.entities.Servico
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.UUID

class ServicoApplicationMapperTest {
    private val mapper = ServicoApplicationMapper()

    @Test
    fun `deve converter request de criacao para input`() {
        val request =
            CriarServicoRequest(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            )

        val resultado = mapper.fromRequest(request)

        assertEquals(
            CriarServicoInput(
                descricao = "Troca de óleo",
                valor = BigDecimal("150.00"),
            ),
            resultado,
        )
    }

    @Test
    fun `deve converter request de atualizacao para input`() {
        val request =
            AtualizarServicoRequest(
                descricao = "Troca de óleo completa",
                valor = BigDecimal("180.00"),
            )

        val resultado = mapper.fromRequest(request)

        assertEquals(
            AtualizarServicoInput(
                descricao = "Troca de óleo completa",
                valor = BigDecimal("180.00"),
            ),
            resultado,
        )
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
