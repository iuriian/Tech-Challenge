package br.com.fiap.oficina.application.usecase.cliente

import br.com.fiap.oficina.application.port.out.ClienteRepository
import br.com.fiap.oficina.domain.entity.Cliente
import org.springframework.stereotype.Service

@Service
class BuscarClientePorNomeUseCase(
    private val clienteRepository: ClienteRepository
) {
    fun executar(nome: String): Cliente? = clienteRepository.buscarPorNome(nome)
}
