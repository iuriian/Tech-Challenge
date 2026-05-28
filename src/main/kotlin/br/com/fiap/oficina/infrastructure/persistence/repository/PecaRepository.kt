package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.infrastructure.persistence.entity.Peca
import org.springframework.data.jpa.repository.JpaRepository

interface PecaRepository : JpaRepository<Peca, Long> {

    fun findAllByAtivoTrue(): List<Peca>

    fun findByCodigoAndAtivoTrue(codigo: String): Peca?

    fun findByNomeIgnoreCaseAndAtivoTrue(nome: String): Peca?

    fun existsByCodigoAndAtivoTrue(codigo: String): Boolean

    fun findByCodigo(codigo: String): Peca?

    fun existsByCodigo(codigo: String): Boolean

}
