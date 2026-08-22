package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
class CriarFuncionarioUseCase(private val funcionarioRepository: FuncionarioRepository) {
    fun executar(funcionario: Funcionario): Funcionario = funcionarioRepository.salvar(funcionario)
}
