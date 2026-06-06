package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.presentation.dto.ServicoDto
import org.springframework.stereotype.Component

@Component
class ServicoMapper {

    fun toResponse(servico: Servico): ServicoDto {
        return ServicoDto(
            id = servico.id,
            descricao = servico.descricao,
            status = servico.status,
            funcionarioId = servico.funcionarioId ?: 0L,
            clienteId = servico.cliente.id ?: 0L,
            veiculoId = servico.veiculo.idVeiculo ?: 0L,
            pecasIds = servico.pecas.map { it.id ?: 0L }
        )
    }

    fun toEntity(dto: ServicoDto): Servico {
        return Servico().apply {
            id = dto.id
            descricao = dto.descricao
            status = dto.status
            funcionarioId = dto.funcionarioId
        }
    }
}