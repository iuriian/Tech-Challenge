package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.jpa.ServicoJpaRepository
import org.springframework.stereotype.Component

@Component
class ServicoRepositoryAdapter(
    private val jpaRepository: ServicoJpaRepository,
    private val mapper: ServicoPersistenceMapper,
) : ServicoRepository {
    override fun salvar(servico: Servico): Servico = mapper.toDomain(jpaRepository.save(mapper.toJpa(servico)))

    override fun buscarPorId(id: Id): Servico? = jpaRepository.findById(id.valor).map(mapper::toDomain).orElse(null)

    override fun listarTodos(): List<Servico> = jpaRepository.findAll().map(mapper::toDomain)

    override fun listarPorCliente(clienteId: Id): List<Servico> =
        jpaRepository.findByClienteId(clienteId.valor).map(mapper::toDomain)

    override fun existePorId(id: Id): Boolean = jpaRepository.existsById(id.valor)

    override fun deletarPorId(id: Id) = jpaRepository.deleteById(id.valor)
}
