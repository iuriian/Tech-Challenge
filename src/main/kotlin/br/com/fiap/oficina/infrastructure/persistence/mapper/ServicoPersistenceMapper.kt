package br.com.fiap.oficina.infrastructure.persistence.mapper

import br.com.fiap.oficina.domain.entity.PecaServico
import br.com.fiap.oficina.domain.entity.Servico
import br.com.fiap.oficina.domain.enum.ServicoStatus
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.entity.PecaServicoJpaEntity
import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import org.springframework.stereotype.Component

@Component
class ServicoPersistenceMapper(
    private val clienteMapper: ClientePersistenceMapper,
    private val veiculoMapper: VeiculoPersistenceMapper,
    private val pecaMapper: PecaPersistenceMapper
) {

    fun toDomain(entity: ServicoJpaEntity): Servico =
        Servico(
            id = Id.from(entity.id),
            descricao = entity.descricao,
            status = entity.status ?: ServicoStatus.RECEBIDA,
            funcionarioId = entity.funcionarioId ?: 0L,
            cliente = clienteMapper.toDomain(entity.cliente),
            veiculo = veiculoMapper.toDomain(entity.veiculo),
            pecas = entity.pecas.map {
                PecaServico(peca = pecaMapper.toDomain(it.peca), quantidade = it.quantidade)
            },
            dataAbertura = entity.dataAbertura,
            dataInicioExecucao = entity.dataInicioExecucao,
            dataFinalizacao = entity.dataFinalizacao
        )

    fun toJpa(domain: Servico): ServicoJpaEntity {
        val entity = ServicoJpaEntity().apply {
            id = domain.id.valor
            descricao = domain.descricao
            status = domain.status
            funcionarioId = domain.funcionarioId
            cliente = clienteMapper.toJpa(domain.cliente)
            veiculo = veiculoMapper.toJpa(domain.veiculo)
            dataAbertura = domain.dataAbertura
            dataInicioExecucao = domain.dataInicioExecucao
            dataFinalizacao = domain.dataFinalizacao
        }
        entity.pecas = domain.pecas.map { pecaServico ->
            PecaServicoJpaEntity().apply {
                servico = entity
                peca = pecaMapper.toJpa(pecaServico.peca)
                quantidade = pecaServico.quantidade
            }
        }.toMutableList()
        return entity
    }
}
