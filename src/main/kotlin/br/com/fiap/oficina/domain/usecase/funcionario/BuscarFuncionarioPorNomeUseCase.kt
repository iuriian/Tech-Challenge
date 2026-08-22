package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import org.springframework.stereotype.Service

@Service
class BuscarFuncionarioPorNomeUseCase(private val funcionarioRepository: FuncionarioRepository) {
    fun executar(nome: String): Funcionario? = funcionarioRepository.buscarPorNome(nome)
}
