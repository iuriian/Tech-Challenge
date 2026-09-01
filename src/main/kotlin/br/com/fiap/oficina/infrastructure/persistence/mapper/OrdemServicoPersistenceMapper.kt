package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.domain.valueobject.ItemOrcamento
import br.com.fiap.oficina.domain.valueobject.NumeroOrdemServico
import br.com.fiap.oficina.domain.valueobject.Orcamento
import br.com.fiap.oficina.infrastructure.persistence.entity.ItemOrcamentoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.OrdemServicoJpaEntity
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class OrdemServicoPersistenceMapper {
    fun toDomain(entity: OrdemServicoJpaEntity): OrdemServico = OrdemServico(
        id = Id(entity.id),
        osNumber = entity.osNumber?.let { NumeroOrdemServico(it) },
        descricao = entity.descricao,
        status = entity.status,
        funcionarioId = Id(entity.funcionarioId),
        clienteId = Id(entity.clienteId),
        veiculoId = Id(entity.veiculoId),
        orcamento =
        Orcamento(
            itens =
            entity.itens.map { item ->
                ItemOrcamento(
                    tipo = item.tipo,
                    referenciaId = Id(item.referenciaId),
                    descricao = item.descricao,
                    valorUnitario = item.valorUnitario,
                    quantidade = item.quantidade,
                    codigoReferencia = item.codigoReferencia,
                )
            },
        ),
        prazo = entity.prazoMinutos?.let { Duration.ofMinutes(it) },
        dataAbertura = entity.dataAbertura,
        dataInicioExecucao = entity.dataInicioExecucao,
        dataFinalizacao = entity.dataFinalizacao,
    )

    fun toJpa(domain: OrdemServico): OrdemServicoJpaEntity {
        val entity =
            OrdemServicoJpaEntity(
                id = domain.id.valor,
                osNumber = domain.osNumber?.valor,
                prazoMinutos = domain.prazo?.toMinutes(),
                descricao = domain.descricao,
                status = domain.status,
                funcionarioId = domain.funcionarioId.valor,
                clienteId = domain.clienteId.valor,
                veiculoId = domain.veiculoId.valor,
                dataAbertura = domain.dataAbertura,
                dataInicioExecucao = domain.dataInicioExecucao,
                dataFinalizacao = domain.dataFinalizacao,
            )

        entity.itens =
            domain.orcamento.itens
                .map { item ->
                    ItemOrcamentoJpaEntity().apply {
                        ordemServico = entity
                        tipo = item.tipo
                        referenciaId = item.referenciaId.valor
                        descricao = item.descricao
                        valorUnitario = item.valorUnitario
                        quantidade = item.quantidade
                        codigoReferencia = item.codigoReferencia
                    }
                }
                .toMutableList()

        return entity
    }
}
