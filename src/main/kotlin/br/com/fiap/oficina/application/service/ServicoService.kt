package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.application.dto.ServicoRequest
import br.com.fiap.oficina.application.dto.ServicoResponse
import br.com.fiap.oficina.application.mapper.ServicoMapper
import br.com.fiap.oficina.domain.usecase.servico.AtualizarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.BuscarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.CriarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.DesativarServicoUseCase
import br.com.fiap.oficina.domain.usecase.servico.ListarServicosAtivosUseCase
import br.com.fiap.oficina.domain.usecase.servico.ListarTodosServicosUseCase
import br.com.fiap.oficina.domain.usecase.servico.ReativarServicoUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ServicoService(
    private val criarServicoUseCase: CriarServicoUseCase,
    private val buscarServicoUseCase: BuscarServicoUseCase,
    private val atualizarServicoUseCase: AtualizarServicoUseCase,
    private val listarServicosAtivosUseCase: ListarServicosAtivosUseCase,
    private val listarTodosServicosUseCase: ListarTodosServicosUseCase,
    private val desativarServicoUseCase: DesativarServicoUseCase,
    private val reativarServicoUseCase: ReativarServicoUseCase,
    private val mapper: ServicoMapper,
) {
    fun criar(request: ServicoRequest): ServicoResponse {
        val servico = mapper.toDomain(request)
        val response = criarServicoUseCase.executar(servico)

        return mapper.toResponse(response)
    }

    fun listarAtivos(): List<ServicoResponse> = listarServicosAtivosUseCase
        .executar()
        .map(mapper::toResponse)

    fun listarTodos(): List<ServicoResponse> = listarTodosServicosUseCase
        .executar()
        .map(mapper::toResponse)

    fun buscar(id: UUID): ServicoResponse = mapper.toResponse(
        buscarServicoUseCase.executar(
            Id(id),
        ),
    )

    fun atualizar(id: UUID, request: ServicoRequest): ServicoResponse {
        val servico = mapper.toDomain(
            id = Id(id),
            request = request,
        )

        return mapper.toResponse(
            atualizarServicoUseCase.executar(servico),
        )
    }

    fun desativar(id: UUID) {
        desativarServicoUseCase.executar(Id(id))
    }

    fun reativar(id: UUID): ServicoResponse = mapper.toResponse(
        reativarServicoUseCase.executar(
            Id(id),
        ),
    )
}
