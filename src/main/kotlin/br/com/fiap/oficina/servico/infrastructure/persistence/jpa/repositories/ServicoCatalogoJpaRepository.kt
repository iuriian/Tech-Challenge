package br.com.fiap.oficina.servico.infrastructure.persistence.jpa.repositories

import br.com.fiap.oficina.servico.infrastructure.persistence.jpa.entities.ServicoCatalogoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ServicoCatalogoJpaRepository : JpaRepository<ServicoCatalogoJpaEntity, UUID> {
    fun findAllByAtivoTrue(): List<ServicoCatalogoJpaEntity>
}
