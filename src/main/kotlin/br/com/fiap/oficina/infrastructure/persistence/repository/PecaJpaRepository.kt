package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.infrastructure.persistence.entity.PecaJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface PecaJpaRepository : JpaRepository<PecaJpaEntity, Long> {

    fun findAllByAtivoTrue(): List<PecaJpaEntity>

    fun findByCodigoAndAtivoTrue(codigo: String): PecaJpaEntity?

    fun findByNomeIgnoreCaseAndAtivoTrue(nome: String): PecaJpaEntity?

    fun existsByCodigoAndAtivoTrue(codigo: String): Boolean

    fun findByCodigo(codigo: String): PecaJpaEntity?

    fun existsByCodigo(codigo: String): Boolean

}
