package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.repository.ClienteRepository
import org.springframework.stereotype.Service

@Service
class ClienteService(private val repository: ClienteRepository) {

    fun salvarCliente(cliente: Cliente): Cliente = repository.salvar(cliente)


    fun buscarPorId(id: Long): Cliente? = repository.buscarPorId(id)

    fun buscarPorDocumento(documentoNumero: String): Cliente? =
        repository.buscarPorDocumento(documentoNumero)

    fun buscarPorNome(nome: String): Cliente? = repository.buscarPorNome(nome)

    fun removerCliente(id: Long) {
        this.repository.remover(id)
    }

}
