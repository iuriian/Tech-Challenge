package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.result.TempoMedioExecucaoResult
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TempoMedioExecucaoMapperTest {
    private val mapper = TempoMedioExecucaoMapper()

    @Test
    fun `deve mapear resultado de tempo medio para response`() {
        val resultado =
            TempoMedioExecucaoResult(
                totalOrdensFinalizadas = 2,
                tempoMedioMinutos = 150.0,
            )

        val response = mapper.toResponse(resultado)

        assertEquals(2, response.totalOrdensFinalizadas)
        assertEquals(150.0, response.tempoMedioMinutos)
    }
}
