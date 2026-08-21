package br.com.fiap.oficina.domain.usecase.cliente

import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.entity.Cliente
import org.springframework.stereotype.Service

@Service
class BuscarClientePorNomeUseCase(
    private val clienteRepository: ClienteRepository
) {
    fun executar(nome: String): Cliente? = clienteRepository.buscarPorNome(nome)
}
