package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.repository.OrdemServicoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.ServicoJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoPersistenceMapper
import org.springframework.stereotype.Component

@Component
class ServicoRepositoryAdapter(
    private val jpaRepository: ServicoJpaRepository,
    private val mapper: ServicoPersistenceMapper,
) : OrdemServicoRepository {
    override fun salvar(ordemServico: OrdemServico): OrdemServico =
        mapper.toDomain(jpaRepository.save(mapper.toJpa(ordemServico)))

    override fun buscarPorId(id: Id): OrdemServico? =
        jpaRepository.findById(id.valor).map(mapper::toDomain).orElse(null)

    override fun listarTodos(): List<OrdemServico> = jpaRepository.findAll().map(mapper::toDomain)

    override fun listarPorCliente(clienteId: Id): List<OrdemServico> =
        jpaRepository.findByClienteId(clienteId.valor).map(mapper::toDomain)

    override fun existePorId(id: Id): Boolean = jpaRepository.existsById(id.valor)

    override fun deletarPorId(id: Id) = jpaRepository.deleteById(id.valor)
}
