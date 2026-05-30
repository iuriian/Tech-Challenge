package br.com.fiap.oficina.infrastructure.persistence.adapter

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.repository.ServicoJpaRepository
import org.springframework.stereotype.Component

@Component
class ServicoRepositoryAdapter(
    private val jpaRepository: ServicoJpaRepository,
    private val mapper: ServicoPersistenceMapper
) : ServicoRepository {

    override fun salvar(servico: Servico): Servico =
        mapper.toDomain(jpaRepository.save(mapper.toJpa(servico)))

    override fun buscarPorId(id: Long): Servico? =
        jpaRepository.findById(id).map(mapper::toDomain).orElse(null)

    override fun listarTodos(): List<Servico> =
        jpaRepository.findAll().map(mapper::toDomain)

    override fun existePorId(id: Long): Boolean =
        jpaRepository.existsById(id)

    override fun deletarPorId(id: Long) = jpaRepository.deleteById(id)
}
