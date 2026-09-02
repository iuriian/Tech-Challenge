package br.com.fiap.oficina.application.mapper

import br.com.fiap.oficina.application.dto.ServicoRequest
import br.com.fiap.oficina.application.dto.ServicoResponse
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Component

@Component
class ServicoMapper {
    fun toDomain(request: ServicoRequest): Servico = Servico.criar(
        descricao = request.descricao,
        valor = request.valor,
    )

    fun toDomain(id: Id, request: ServicoRequest): Servico = Servico(
        id = id,
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
