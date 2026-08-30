package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Id

class BuscarClientePorIdUseCase(private val clienteRepository: ClienteRepository) {
    fun executar(id: Id): Cliente = clienteRepository.buscarPorId(id)
        ?: throw ClienteNaoEncontradoException.porId(id.valor.toString())
}
