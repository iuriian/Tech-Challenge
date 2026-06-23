package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface ServicoJpaRepository : JpaRepository<ServicoJpaEntity, UUID> {
    fun findByClienteId(clienteId: UUID): List<ServicoJpaEntity>
}
