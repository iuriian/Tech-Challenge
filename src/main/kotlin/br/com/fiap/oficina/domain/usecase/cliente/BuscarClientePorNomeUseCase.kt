package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ClienteRepository

class BuscarClientePorNomeUseCase(private val clienteRepository: ClienteRepository) {
    fun executar(nome: String): Cliente = clienteRepository.buscarPorNome(nome)
        ?: throw ClienteNaoEncontradoException.porNome(nome)
}
