package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.infrastructure.persistence.entity.PecaJpaEntity
import org.mapstruct.Mapper

@Mapper(componentModel = "spring")
interface PecaPersistenceMapper {

    fun toDomain(entity: PecaJpaEntity): Peca

    fun toJpa(domain: Peca): PecaJpaEntity
}
