package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.application.dto.AtualizarClienteRequest
import br.com.fiap.oficina.application.dto.ClienteResponse
import br.com.fiap.oficina.application.dto.CriarClienteRequest
import br.com.fiap.oficina.application.mapper.ClienteApplicationMapper
import br.com.fiap.oficina.domain.usecase.cliente.AtualizarClienteUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorDocumentoUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorIdUseCase
import br.com.fiap.oficina.domain.usecase.cliente.BuscarClientePorNomeUseCase
import br.com.fiap.oficina.domain.usecase.cliente.CriarClienteUseCase
import br.com.fiap.oficina.domain.usecase.cliente.ListarClientesUseCase
import br.com.fiap.oficina.domain.usecase.cliente.RemoverClienteUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class ClienteService(
    private val criarClienteUseCase: CriarClienteUseCase,
    private val listarClientesUseCase: ListarClientesUseCase,
    private val buscarClientePorIdUseCase: BuscarClientePorIdUseCase,
    private val buscarClientePorNomeUseCase: BuscarClientePorNomeUseCase,
    private val buscarClientePorDocumentoUseCase: BuscarClientePorDocumentoUseCase,
    private val atualizarClienteUseCase: AtualizarClienteUseCase,
    private val removerClienteUseCase: RemoverClienteUseCase,
    private val mapper: ClienteApplicationMapper,
) {
    fun criar(request: CriarClienteRequest): ClienteResponse {
        val cliente = mapper.toDomain(request)
        val response = criarClienteUseCase.executar(cliente)
        return mapper.toResponse(response)
    }

    fun listarTodos(): List<ClienteResponse> = listarClientesUseCase.executar().map { mapper.toResponse(it) }

    fun buscarPorId(id: String): ClienteResponse {
        val resultado = buscarClientePorIdUseCase.executar(Id.fromString(id))
        return mapper.toResponse(resultado)
    }

    fun buscarPorNome(nome: String): ClienteResponse {
        val resultado = buscarClientePorNomeUseCase.executar(nome)
        return mapper.toResponse(resultado)
    }

    fun buscarPorDocumento(documento: String): ClienteResponse {
        val resultado = buscarClientePorDocumentoUseCase.executar(documento)
        return mapper.toResponse(resultado)
    }

    fun alterar(id: String, request: AtualizarClienteRequest): ClienteResponse {
        val cliente = mapper.toDomain(id, request)
        val response = atualizarClienteUseCase.executar(cliente)
        return mapper.toResponse(response)
    }

    fun remover(id: String) = removerClienteUseCase.executar(Id.fromString(id))
}
