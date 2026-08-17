package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface ServicoJpaRepository : JpaRepository<ServicoJpaEntity, UUID> {
    fun findByStatus(status: OrdemServicoStatus): List<ServicoJpaEntity>

    fun findByClienteId(clienteId: UUID): List<ServicoJpaEntity>

    fun findByVeiculo_IdVeiculo(veiculoId: UUID): List<ServicoJpaEntity>

    fun findByDataAberturaBetween(
        inicio: Instant,
        fim: Instant
    ): List<ServicoJpaEntity>

}
