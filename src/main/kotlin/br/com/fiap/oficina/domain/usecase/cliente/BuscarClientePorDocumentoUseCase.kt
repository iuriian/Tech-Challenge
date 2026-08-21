package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.repository.ClienteRepository
import org.springframework.stereotype.Service

@Service
class BuscarClientePorDocumentoUseCase(private val clienteRepository: ClienteRepository) {
    fun executar(numeroDocumento: String): Cliente? = clienteRepository.buscarPorDocumento(numeroDocumento)
}
