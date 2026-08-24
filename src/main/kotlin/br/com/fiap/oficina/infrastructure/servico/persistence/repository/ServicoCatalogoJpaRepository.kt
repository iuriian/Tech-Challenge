package br.com.fiap.oficina.infrastructure.servico.persistence.repository

import br.com.fiap.oficina.infrastructure.servico.persistence.entity.ServicoCatalogoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ServicoCatalogoJpaRepository : JpaRepository<ServicoCatalogoJpaEntity, UUID> {
    fun findAllByAtivoTrue(): List<ServicoCatalogoJpaEntity>
}
