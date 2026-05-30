package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring", uses = [ClientePersistenceMapper::class])
interface ServicoPersistenceMapper {

    fun toDomain(entity: ServicoJpaEntity): Servico

    fun toJpa(domain: Servico): ServicoJpaEntity
}
