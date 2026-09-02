package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.application.dto.FuncionarioRequest
import br.com.fiap.oficina.application.dto.FuncionarioResponse
import br.com.fiap.oficina.application.mapper.FuncionarioMapper
import br.com.fiap.oficina.domain.usecase.funcionario.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.CriarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.ListarFuncionariosUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.RemoverFuncionarioUseCase
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class FuncionarioService(
    private val criarFuncionarioUseCase: CriarFuncionarioUseCase,
    private val listarFuncionariosUseCase: ListarFuncionariosUseCase,
    private val buscarFuncionarioPorIdUseCase: BuscarFuncionarioPorIdUseCase,
    private val buscarFuncionarioPorNomeUseCase: BuscarFuncionarioPorNomeUseCase,
    private val atualizarFuncionarioUseCase: AtualizarFuncionarioUseCase,
    private val removerFuncionarioUseCase: RemoverFuncionarioUseCase,
    private val mapper: FuncionarioMapper,
) {
    fun cadastrar(request: FuncionarioRequest): FuncionarioResponse {
        val funcionario = mapper.toDomain(request)
        val response = criarFuncionarioUseCase.executar(funcionario)
        return mapper.toResponse(response)
    }

    fun listarTodos(): List<FuncionarioResponse> {
        val response = listarFuncionariosUseCase.executar()
        return response.map { mapper.toResponse(it) }
    }

    fun buscarPorId(id: String): FuncionarioResponse {
        val resultado = buscarFuncionarioPorIdUseCase.executar(Id.fromString(id))
        return mapper.toResponse(resultado)
    }

    fun buscarPorNome(nome: String): FuncionarioResponse {
        val resultado = buscarFuncionarioPorNomeUseCase.executar(nome)
        return mapper.toResponse(resultado)
    }

    fun editar(id: String, request: FuncionarioRequest): FuncionarioResponse {
        val funcionario = mapper.toDomain(request.copy(id = id))
        val response = atualizarFuncionarioUseCase.executar(funcionario)
        return mapper.toResponse(response)
    }

    fun deletar(id: String) = removerFuncionarioUseCase.executar(Id.fromString(id))
}
