package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.exception.FuncionarioNaoEncontradoException
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id

class RemoverFuncionarioUseCase(private val funcionarioRepository: FuncionarioRepository) {
    fun executar(id: Id) {
        funcionarioRepository.buscarPorId(id)
            ?: throw FuncionarioNaoEncontradoException.porId(id.valor.toString())
        funcionarioRepository.deletar(id)
    }
}
