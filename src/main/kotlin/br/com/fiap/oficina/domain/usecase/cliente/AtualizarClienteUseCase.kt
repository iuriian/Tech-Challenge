package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ClienteRepository

class AtualizarClienteUseCase(private val clienteRepository: ClienteRepository) {
    fun executar(cliente: Cliente): Cliente {
        clienteRepository.buscarPorId(cliente.id)
            ?: throw ClienteNaoEncontradoException.porId(cliente.id.valor.toString())

        return clienteRepository.salvar(cliente)
    }
}
