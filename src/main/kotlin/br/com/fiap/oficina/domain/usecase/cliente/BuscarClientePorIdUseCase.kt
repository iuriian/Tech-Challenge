package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class BuscarClientePorIdUseCase(
    private val clienteRepository: ClienteRepository
) {
    fun executar(id: Id): Cliente? = clienteRepository.buscarPorId(id)
}
