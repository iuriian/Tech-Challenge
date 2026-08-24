package br.com.fiap.oficina.infrastructure.persistence.repository.servico

import br.com.fiap.oficina.infrastructure.persistence.entity.servico.ServicoCatalogoJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ServicoCatalogoJpaRepository :
    JpaRepository<ServicoCatalogoJpaEntity, UUID> {
    fun findAllByAtivoTrue(): List<ServicoCatalogoJpaEntity>
}