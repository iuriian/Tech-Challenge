package br.com.fiap.oficina.infrastructure.persistence.adapter

import br.com.fiap.oficina.application.port.out.ServicoRepository
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.infrastructure.persistence.mapper.ServicoCatalogoPersistenceMapper
import br.com.fiap.oficina.infrastructure.persistence.repository.ServicoJpaRepository
import org.springframework.stereotype.Component

@Component
class ServicoRepositoryAdapter(
    private val jpaRepository: ServicoJpaRepository,
    private val mapper: ServicoCatalogoPersistenceMapper,
) : ServicoRepository {
    override fun salvar(servico: Servico): Servico =
        mapper.toDomain(
            jpaRepository.save(
                mapper.toJpa(servico),
            ),
        )
}
