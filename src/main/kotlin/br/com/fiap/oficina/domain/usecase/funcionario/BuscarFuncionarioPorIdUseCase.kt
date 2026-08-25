package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.exception.FuncionarioNaoEncontradoException
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id

class BuscarFuncionarioPorIdUseCase(private val funcionarioRepository: FuncionarioRepository) {
    fun executar(id: Id): Funcionario =
        funcionarioRepository.buscarPorId(id)
            ?: throw FuncionarioNaoEncontradoException.porId(id.valor.toString())
}
