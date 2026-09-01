package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.AtualizarServicoRequest
import br.com.fiap.oficina.application.dto.CriarServicoRequest
import br.com.fiap.oficina.application.dto.ServicoResponse
import br.com.fiap.oficina.domain.usecase.servico.AtualizarServicoInput
import br.com.fiap.oficina.domain.usecase.servico.CriarServicoInput
import br.com.fiap.oficina.servico.domain.entities.Servico
import org.springframework.stereotype.Component

@Component
class ServicoApplicationMapper {
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
