package br.com.fiap.oficina.domain.usecase.cliente
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.entity.Cliente
import org.springframework.stereotype.Service

@Service
class CriarClienteUseCase(
    private val clienteRepository: ClienteRepository
) {
    fun executar(cliente: Cliente): Cliente = clienteRepository.salvar(cliente)
}
