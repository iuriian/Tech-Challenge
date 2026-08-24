package br.com.fiap.oficina.infrastructure.servico.persistence.mapper

import br.com.fiap.oficina.domain.servico.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.servico.persistence.entity.ServicoCatalogoJpaEntity
import org.springframework.stereotype.Component

@Component
class ServicoCatalogoPersistenceMapper {
    fun toDomain(entity: ServicoCatalogoJpaEntity): Servico =
        Servico(
            id = Id(entity.id),
            descricao = entity.descricao,
            valor = entity.valor,
            ativo = entity.ativo,
        )

    fun toJpaEntity(domain: Servico): ServicoCatalogoJpaEntity =
        ServicoCatalogoJpaEntity(
            id = domain.id.valor,
            descricao = domain.descricao,
            valor = domain.valor,
            ativo = domain.ativo,
        )
}