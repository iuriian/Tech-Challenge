package br.com.fiap.oficina.servico.infrastructure.persistence.mappers

import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.mapper.PecaPersistenceMapper
import br.com.fiap.oficina.servico.domain.entities.OrdemServico
import br.com.fiap.oficina.servico.domain.entities.PecaServico
import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.domain.valueobjects.NumeroOrdemServico
import br.com.fiap.oficina.servico.infrastructure.persistence.jpa.entities.PecaServicoJpaEntity
import br.com.fiap.oficina.servico.infrastructure.persistence.jpa.entities.ServicoJpaEntity
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class ServicoPersistenceMapper(private val pecaMapper: PecaPersistenceMapper) {
    fun toDomain(entity: ServicoJpaEntity): OrdemServico = OrdemServico(
        id = Id(entity.id),
        osNumber = entity.osNumber?.let { NumeroOrdemServico(it) },
        descricao = entity.descricao,
        status = entity.status ?: OrdemServicoStatus.RECEBIDA,
        funcionarioId = Id(entity.funcionarioId),
        clienteId = Id(entity.clienteId),
        veiculoId = Id(entity.veiculoId),
        pecas =
        entity.pecas.map {
            PecaServico(
                peca = pecaMapper.toDomain(it.peca),
                quantidade = it.quantidade,
            )
        },
        prazo = entity.prazoMinutos?.let { Duration.ofMinutes(it) },
        dataAbertura = entity.dataAbertura,
        dataInicioExecucao = entity.dataInicioExecucao,
        dataFinalizacao = entity.dataFinalizacao,
    )

    fun toJpa(domain: OrdemServico): ServicoJpaEntity {
        val entity =
            ServicoJpaEntity(
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

        entity.pecas =
            domain.pecas
                .map { pecaServico ->
                    PecaServicoJpaEntity().apply {
                        servico = entity
                        peca = pecaMapper.toJpa(pecaServico.peca)
                        quantidade = pecaServico.quantidade
                    }
                }.toMutableList()

        return entity
    }
}
