package br.com.fiap.oficina.infrastructure.persistence.adapter

import br.com.fiap.oficina.domain.entity.Cliente
import br.com.fiap.oficina.application.port.out.ClienteRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.mapper.ClientePersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.repository.ClienteJpaRepository
import org.springframework.stereotype.Component

@Component
class ClienteRepositoryAdapter(
    private val jpaRepository: ClienteJpaRepository,
    private val mapper: ClientePersistenceMapper
) : ClienteRepository {

    override fun salvar(cliente: Cliente): Cliente =
        mapper.toDomain(jpaRepository.save(mapper.toJpa(cliente)))

    override fun buscarPorId(id: Id): Cliente? =
        jpaRepository.findById(id.valor).map(mapper::toDomain).orElse(null)

    override fun buscarPorDocumento(numeroDocumento: String): Cliente? =
        jpaRepository.findByDocumentoNumero(numeroDocumento)?.let(mapper::toDomain)

    override fun buscarPorNome(nome: String): Cliente? =
        jpaRepository.findByNome(nome)?.let(mapper::toDomain)

    override fun listarTodos(): List<Cliente> =
        jpaRepository.findAll().map(mapper::toDomain)

    override fun remover(id: Id) = jpaRepository.deleteById(id.valor)
}
