package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import org.springframework.stereotype.Component

@Component
class ServicoPersistenceMapper {
    fun toDomain(entity: ServicoJpaEntity): Servico = Servico(
        id = Id(entity.id),
        descricao = entity.descricao,
        valor = entity.valor,
        ativo = entity.ativo,
    )

    fun toJpaEntity(domain: Servico): ServicoJpaEntity = ServicoJpaEntity(
        id = domain.id.valor,
        descricao = domain.descricao,
        valor = domain.valor,
        ativo = domain.ativo,
    )
}
