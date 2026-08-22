package br.com.fiap.oficina.infrastructure.persistence.jpa

import br.com.fiap.oficina.infrastructure.persistence.entity.PecaJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PecaJpaRepository : JpaRepository<PecaJpaEntity, UUID> {

    fun findAllByAtivoTrue(): List<PecaJpaEntity>

    fun findByCodigoAndAtivoTrue(codigo: String): PecaJpaEntity?

    fun findByNomeIgnoreCaseAndAtivoTrue(nome: String): PecaJpaEntity?

    fun existsByCodigoAndAtivoTrue(codigo: String): Boolean

    fun findByCodigo(codigo: String): PecaJpaEntity?

    fun existsByCodigo(codigo: String): Boolean
}
