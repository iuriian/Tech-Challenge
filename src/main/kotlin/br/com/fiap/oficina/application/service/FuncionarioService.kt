package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.usecase.funcionario.AtualizarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorIdUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.BuscarFuncionarioPorNomeUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.CriarFuncionarioUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.ListarFuncionariosUseCase
import br.com.fiap.oficina.domain.usecase.funcionario.RemoverFuncionarioUseCase
import br.com.fiap.oficina.presentation.dto.FuncionarioDto
import br.com.fiap.oficina.presentation.mapper.FuncionarioMapper
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
    fun cadastrar(dto: FuncionarioDto): FuncionarioDto {
        val funcionario = mapper.toEntity(dto)

        val response = criarFuncionarioUseCase.executar(funcionario)
        return mapper.toResponse(response)
    }

    fun listarTodos(): List<FuncionarioDto> {
        val response = listarFuncionariosUseCase.executar()
        return response.map { mapper.toResponse(it) }
    }

    fun buscarPorId(id: String): FuncionarioDto? {
        val resultado = buscarFuncionarioPorIdUseCase.executar(id)

        return if (resultado != null) {
            mapper.toResponse(resultado)
        } else {
            null
        }
    }

    fun buscarPorNome(nome: String): FuncionarioDto? {
        val resultado = buscarFuncionarioPorNomeUseCase.executar(nome) // repository.buscarPorNome(nome)

        return if (resultado != null) {
            mapper.toResponse(resultado)
        } else {
            null
        }
    }

    fun editar(id: String, dto: FuncionarioDto): FuncionarioDto {
        val funcionario = mapper.toEntityComId(id, dto)

        val response = atualizarFuncionarioUseCase.executar(funcionario)
        return mapper.toResponse(response)
    }

    fun deletar(id: String) = removerFuncionarioUseCase.executar(id)
}
