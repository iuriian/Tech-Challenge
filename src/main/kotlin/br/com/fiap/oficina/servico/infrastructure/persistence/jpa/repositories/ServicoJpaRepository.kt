package br.com.fiap.oficina.servico.infrastructure.persistence.jpa.repositories

import br.com.fiap.oficina.servico.domain.enums.OrdemServicoStatus
import br.com.fiap.oficina.servico.infrastructure.persistence.jpa.entities.ServicoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface ServicoJpaRepository : JpaRepository<ServicoJpaEntity, UUID> {
    fun findByStatus(status: OrdemServicoStatus): List<ServicoJpaEntity>

    fun findByClienteId(clienteId: UUID): List<ServicoJpaEntity>

    fun findByVeiculoId(veiculoId: UUID): List<ServicoJpaEntity>

    fun findByDataAberturaBetween(inicio: Instant, fim: Instant): List<ServicoJpaEntity>
}
