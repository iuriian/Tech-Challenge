package br.com.fiap.oficina.servico.infrastructure.persistence.repositories

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.servico.domain.entities.Servico
import br.com.fiap.oficina.servico.domain.repositories.ServicoRepository
import br.com.fiap.oficina.servico.infrastructure.persistence.jpa.repositories.ServicoCatalogoJpaRepository
import br.com.fiap.oficina.servico.infrastructure.persistence.mappers.ServicoCatalogoPersistenceMapper
import org.springframework.stereotype.Component

@Component
class ServicoCatalogoRepositoryAdapter(
    private val jpaRepository: ServicoCatalogoJpaRepository,
    private val mapper: ServicoCatalogoPersistenceMapper,
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
