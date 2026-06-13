package br.com.fiap.oficina.presentation.mapper

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.presentation.dto.ServicoDto
import org.springframework.stereotype.Component

@Component
class ServicoMapper {

    fun toResponse(servico: Servico): ServicoDto =
        ServicoDto(
            id = servico.id.valor,
            descricao = servico.descricao,
            status = servico.status,
            funcionarioId = servico.funcionarioId,
            clienteId = servico.cliente.id.valor,
            veiculoId = servico.veiculo.id.valor,
            pecasIds = servico.pecas.map { it.id.valor }
        )
}
