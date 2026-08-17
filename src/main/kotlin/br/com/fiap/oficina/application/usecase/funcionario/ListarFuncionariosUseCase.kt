package br.com.fiap.oficina.application.usecase.funcionario

import br.com.fiap.oficina.application.port.out.FuncionarioRepository
import br.com.fiap.oficina.domain.entity.Funcionario
import org.springframework.stereotype.Service

@Service
class ListarFuncionariosUseCase(
    private val funcionarioRepository: FuncionarioRepository,
) {
    fun executar(): List<Funcionario> = funcionarioRepository.listarTodos()
}
