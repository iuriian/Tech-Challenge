package br.com.fiap.oficina.application.usecase.funcionario

import br.com.fiap.oficina.application.port.out.FuncionarioRepository
import br.com.fiap.oficina.domain.entity.Funcionario
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class BuscarFuncionarioPorIdUseCase(
    private val funcionarioRepository: FuncionarioRepository,
) {
    fun executar(id: Id): Funcionario? = funcionarioRepository.buscarPorId(id)
}
