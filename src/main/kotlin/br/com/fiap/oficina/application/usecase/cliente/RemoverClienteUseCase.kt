package br.com.fiap.oficina.application.usecase.cliente
import br.com.fiap.oficina.application.port.out.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class RemoverClienteUseCase(
    private val clienteRepository: ClienteRepository
) {
    fun executar(id: Id) = clienteRepository.remover(id)
}
