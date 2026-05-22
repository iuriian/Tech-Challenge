package br.com.fiap.oficina.application

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.repository.ClienteRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ClienteService(private val repository: ClienteRepository) {

    fun salvarCliente(cliente: Cliente): Cliente {
        return repository.save(cliente)
    }

    fun buscarPorId(id: UUID): Cliente? {
        return repository.findById(id).orElse(null)
    }

    fun buscarPorCpf(cpf: String): Cliente? {
        return repository.findByCpf(cpf)
    }
}