package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Id

class RemoverClienteUseCase(private val clienteRepository: ClienteRepository) {
    fun executar(id: Id) {
        clienteRepository.buscarPorId(id)
            ?: throw ClienteNaoEncontradoException.porId(id.valor.toString())
        clienteRepository.remover(id)
    }
}
