package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.entity.OrdemServico
import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.domain.repository.OrdemServicoRepository
import br.com.fiap.oficina.domain.valueobject.Id
import br.com.fiap.oficina.infrastructure.persistence.jpa.OrdemServicoJpaRepository
import br.com.fiap.oficina.infrastructure.persistence.mapper.OrdemServicoPersistenceMapper
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class OrdemServicoRepositoryAdapter(
    private val jpaRepository: OrdemServicoJpaRepository,
    private val mapper: OrdemServicoPersistenceMapper,
) : OrdemServicoRepository {
    override fun salvar(ordemServico: OrdemServico): OrdemServico = mapper.toDomain(
        jpaRepository.save(
            mapper.toJpa(ordemServico),
        ),
    )

    override fun buscarPorId(id: Id): OrdemServico? = jpaRepository
        .findById(id.valor)
        .map(mapper::toDomain)
        .orElse(null)

    override fun listarTodos(): List<OrdemServico> = jpaRepository
        .findAll()
        .map(mapper::toDomain)

    override fun listarPorStatus(status: OrdemServicoStatus): List<OrdemServico> = jpaRepository
        .findByStatus(status)
        .map(mapper::toDomain)

    override fun listarPorCliente(clienteId: Id): List<OrdemServico> = jpaRepository
        .findByClienteId(clienteId.valor)
        .map(mapper::toDomain)

    override fun listarPorVeiculo(veiculoId: Id): List<OrdemServico> = jpaRepository
        .findByVeiculoId(veiculoId.valor)
        .map(mapper::toDomain)

    override fun listarPorDataAberturaEntre(inicio: Instant, fim: Instant): List<OrdemServico> = jpaRepository
        .findByDataAberturaBetween(inicio, fim)
        .map(mapper::toDomain)

    override fun existePorId(id: Id): Boolean = jpaRepository.existsById(id.valor)

    override fun deletarPorId(id: Id) = jpaRepository.deleteById(id.valor)
}
