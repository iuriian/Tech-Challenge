package br.com.fiap.oficina.application.usecase.funcionario

import br.com.fiap.oficina.application.port.out.FuncionarioRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class RemoverFuncionarioUseCase(
    private val funcionarioRepository: FuncionarioRepository,
) {
    fun executar(id: Id) = funcionarioRepository.deletar(id)
}
