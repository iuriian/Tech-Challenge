package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.entity.Cliente
import org.springframework.stereotype.Service

@Service
class AtualizarClienteUseCase(private val clienteRepository: ClienteRepository) {
    fun executar(cliente: Cliente): Cliente {
        clienteRepository.buscarPorId(cliente.id)
            ?: throw IllegalArgumentException("Cliente não encontrado com o ID: ${cliente.id}")

        return clienteRepository.salvar(cliente)
    }
}
