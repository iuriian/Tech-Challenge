package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.Peca
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.PecaJpaEntity
import org.springframework.stereotype.Component

@Component
class PecaPersistenceMapper {

    fun toDomain(entity: PecaJpaEntity): Peca =
        Peca(
            id = Id.from(entity.id),
            codigo = entity.codigo,
            nome = entity.nome,
            descricao = entity.descricao,
            fabricante = entity.fabricante,
            fornecedor = entity.fornecedor,
            precoDeCompra = entity.precoDeCompra,
            precoDeVenda = entity.precoDeVenda,
            qtdEstoque = entity.qtdEstoque,
            ativo = entity.ativo
        )

    fun toJpa(domain: Peca): PecaJpaEntity =
        PecaJpaEntity(
            id = domain.id.valor,
            codigo = domain.codigo,
            nome = domain.nome,
            descricao = domain.descricao,
            fabricante = domain.fabricante,
            fornecedor = domain.fornecedor,
            precoDeCompra = domain.precoDeCompra,
            precoDeVenda = domain.precoDeVenda,
            qtdEstoque = domain.qtdEstoque,
            ativo = domain.ativo
        )
}
