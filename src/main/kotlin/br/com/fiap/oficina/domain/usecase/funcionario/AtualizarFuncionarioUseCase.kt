package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.exception.FuncionarioNaoEncontradoException
import br.com.fiap.oficina.domain.repository.FuncionarioRepository

class AtualizarFuncionarioUseCase(private val funcionarioRepository: FuncionarioRepository) {
    fun executar(funcionario: Funcionario): Funcionario {
        funcionarioRepository.buscarPorId(funcionario.id)
            ?: throw FuncionarioNaoEncontradoException.porId(funcionario.id.valor.toString())

        return funcionarioRepository.editar(funcionario)
    }
}
