package br.com.fiap.oficina.infrastructure.persistence.jpa

import br.com.fiap.oficina.domain.enum.OrdemServicoStatus
import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface ServicoJpaRepository : JpaRepository<ServicoJpaEntity, UUID> {
    fun findByStatus(status: OrdemServicoStatus): List<ServicoJpaEntity>

    fun findByClienteId(clienteId: UUID): List<ServicoJpaEntity>

    @Query(
        """
            SELECT s
            FROM ServicoJpaEntity s
            WHERE s.veiculo.idVeiculo = :veiculoId
        """,
    )
    fun findByVeiculoId(@Param("veiculoId") veiculoId: UUID): List<ServicoJpaEntity>

    fun findByDataAberturaBetween(inicio: Instant, fim: Instant): List<ServicoJpaEntity>
}
