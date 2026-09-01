package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.repository.ServicoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.ServicoJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoPersistenceMapper
import org.springframework.stereotype.Component

@Component
class ServicoRepositoryAdapter(
    private val jpaRepository: ServicoJpaRepository,
    private val mapper: ServicoPersistenceMapper,
) : ServicoRepository {
    override fun salvar(servico: Servico): Servico = mapper.toDomain(
        jpaRepository.save(
            mapper.toJpaEntity(servico),
        ),
    )

    override fun buscarPorId(id: Id): Servico? = jpaRepository
        .findById(id.valor)
        .map(mapper::toDomain)
        .orElse(null)

    override fun listarAtivos(): List<Servico> = jpaRepository
        .findAllByAtivoTrue()
        .map(mapper::toDomain)

    override fun listarTodos(): List<Servico> = jpaRepository
        .findAll()
        .map(mapper::toDomain)
}
