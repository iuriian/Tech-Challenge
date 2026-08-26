package br.com.fiap.oficina.servico.presentation.mapper

import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.usecases.AtualizarServicoInput
import br.com.fiap.oficina.servico.domain.usecases.CriarServicoInput
import br.com.fiap.oficina.servico.presentation.request.AtualizarServicoRequest
import br.com.fiap.oficina.servico.presentation.request.CriarServicoRequest
import br.com.fiap.oficina.servico.presentation.response.ServicoResponse
import org.springframework.stereotype.Component

@Component
class ServicoPresentationMapper {
    fun fromRequest(request: CriarServicoRequest): CriarServicoInput = CriarServicoInput(
        descricao = request.descricao,
        valor = request.valor,
    )

    fun fromRequest(request: AtualizarServicoRequest): AtualizarServicoInput = AtualizarServicoInput(
        descricao = request.descricao,
        valor = request.valor,
    )

    fun toResponse(servico: Servico): ServicoResponse = ServicoResponse(
        id = servico.id.valor,
        descricao = servico.descricao,
        valor = servico.valor,
        ativo = servico.ativo,
    )
}
