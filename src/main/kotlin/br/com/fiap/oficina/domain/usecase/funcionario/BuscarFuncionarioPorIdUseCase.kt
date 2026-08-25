package br.com.fiap.oficina.domain.usecase.funcionario

import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.repository.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class BuscarFuncionarioPorIdUseCase(private val funcionarioRepository: FuncionarioRepository) {
    fun executar(id: String): Funcionario? = funcionarioRepository.buscarPorId(Id.fromString(id))
}
