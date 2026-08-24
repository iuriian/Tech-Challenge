package br.com.fiap.oficina.infrastructure.persistence.adapter.servico

import br.com.fiap.oficina.application.repository.servico.ServicoRepository
import br.com.fiap.oficina.domain.entity.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.mapper.servico.ServicoCatalogoPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.repository.servico.ServicoCatalogoJpaRepository
import org.springframework.stereotype.Component

@Component
class ServicoCatalogoRepositoryAdapter(
    private val jpaRepository: ServicoCatalogoJpaRepository,
    private val mapper: ServicoCatalogoPersistenceMapper,
) : ServicoRepository {
    override fun salvar(servico: Servico): Servico =
        mapper.toDomain(
            jpaRepository.save(
                mapper.toJpa(servico),
            ),
        )

    override fun buscarPorId(id: Id): Servico? =
        jpaRepository
            .findById(id.valor)
            .map(mapper::toDomain)
            .orElse(null)

    override fun listarAtivos(): List<Servico> =
        jpaRepository
            .findAllByAtivoTrue()
            .map(mapper::toDomain)

    override fun listarTodos(): List<Servico> =
        jpaRepository
            .findAll()
            .map(mapper::toDomain)
}