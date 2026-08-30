package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.exception.ClienteNaoEncontradoException
import br.com.fiap.oficina.domain.repository.ClienteRepository

class BuscarClientePorDocumentoUseCase(private val clienteRepository: ClienteRepository) {
    fun executar(numeroDocumento: String): Cliente = clienteRepository.buscarPorDocumento(numeroDocumento)
        ?: throw ClienteNaoEncontradoException.porDocumento(numeroDocumento)
}
