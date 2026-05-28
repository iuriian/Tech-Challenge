package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.infrastructure.persistence.entity.Servico
import br.com.fiap.oficina.presentation.dto.ServicoDto
import org.springframework.stereotype.Component

@Component
class ServicoMapper {

    fun toResponse(servico: Servico): ServicoDto {
        return ServicoDto(
            id = servico.id,
            descricao = servico.descricao,
            status = servico.status,
            funcionarioId = servico.funcionarioId ?: "",
            clienteId = servico.cliente.id ?: 0L,
            veiculoId = servico.veiculoId ?: 0L,
            pecasIds = servico.pecasIds
        )
    }

    fun toEntity(dto: ServicoDto): Servico {
        return Servico().apply {
            id = dto.id
            descricao = dto.descricao
            status = dto.status
            funcionarioId = dto.funcionarioId
            veiculoId = dto.veiculoId
            pecasIds = dto.pecasIds
        }
    }
}