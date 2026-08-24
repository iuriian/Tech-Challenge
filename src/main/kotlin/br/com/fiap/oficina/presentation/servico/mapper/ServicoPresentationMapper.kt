package br.com.fiap.oficina.presentation.servico.mapper

import br.com.fiap.oficina.application.servico.usecase.AtualizarServicoInput
import br.com.fiap.oficina.application.servico.usecase.CriarServicoInput
import br.com.fiap.oficina.domain.servico.Servico
import br.com.fiap.oficina.presentation.servico.request.AtualizarServicoRequest
import br.com.fiap.oficina.presentation.servico.request.CriarServicoRequest
import br.com.fiap.oficina.presentation.servico.response.ServicoResponse
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
