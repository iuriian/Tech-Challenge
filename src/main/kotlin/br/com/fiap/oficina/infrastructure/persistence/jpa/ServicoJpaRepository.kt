package br.com.fiap.oficina.infrastructure.persistence.jpa

import br.com.fiap.oficina.infrastructure.persistence.entity.ServicoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ServicoJpaRepository : JpaRepository<ServicoJpaEntity, UUID> {
    fun findAllByAtivoTrue(): List<ServicoJpaEntity>
}
