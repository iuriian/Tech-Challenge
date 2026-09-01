package br.com.fiap.oficina.infrastructure.persistence.jpa

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.infrastructure.persistence.entity.OrdemServicoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface OrdemServicoJpaRepository : JpaRepository<OrdemServicoJpaEntity, UUID> {
    fun findByStatus(status: OrdemServicoStatus): List<OrdemServicoJpaEntity>

    fun findByClienteId(clienteId: UUID): List<OrdemServicoJpaEntity>

    fun findByVeiculoId(veiculoId: UUID): List<OrdemServicoJpaEntity>

    fun findByDataAberturaBetween(inicio: Instant, fim: Instant): List<OrdemServicoJpaEntity>
}
