package br.com.fiap.oficina.application.service

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.domain.repository.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Id
import org.springframework.stereotype.Service

@Service
class ClienteService(private val repository: ClienteRepository) {
    fun salvarCliente(cliente: Cliente): Cliente = repository.salvar(cliente)

    fun buscarPorId(id: Id): Cliente? = repository.buscarPorId(id)

    fun buscarPorDocumento(documentoNumero: String): Cliente? = repository.buscarPorDocumento(documentoNumero)

    fun buscarPorNome(nome: String): Cliente? = repository.buscarPorNome(nome)

    fun listarTodos(): List<Cliente> = repository.listarTodos()

    fun removerCliente(id: Id) {
        this.repository.remover(id)
    }
}
