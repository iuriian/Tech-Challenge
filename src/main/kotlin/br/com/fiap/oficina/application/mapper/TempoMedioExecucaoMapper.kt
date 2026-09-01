package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.TempoMedioExecucaoResponse
import br.com.fiap.oficina.application.result.TempoMedioExecucaoResult
import org.springframework.stereotype.Component

@Component
class TempoMedioExecucaoMapper {
    fun toResponse(resultado: TempoMedioExecucaoResult): TempoMedioExecucaoResponse = TempoMedioExecucaoResponse(
        totalOrdensFinalizadas = resultado.totalOrdensFinalizadas,
        tempoMedioMinutos = resultado.tempoMedioMinutos,
    )
}
