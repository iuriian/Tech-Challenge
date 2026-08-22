package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
class ListarFuncionariosUseCase(private val funcionarioRepository: FuncionarioRepository) {
    fun executar(): List<Funcionario> = funcionarioRepository.listarTodos()
}
