package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
class AtualizarFuncionarioUseCase(private val funcionarioRepository: FuncionarioRepository) {
    fun executar(funcionario: Funcionario): Funcionario {
        funcionarioRepository.buscarPorId(funcionario.id)
            ?: throw IllegalArgumentException("Funcionário não encontrado com o ID: ${funcionario.id}")

        return funcionarioRepository.editar(funcionario)
    }
}
