package br.com.fiap.oficina.infrastructure.persistence.jpa.repository

import br.com.fiap.oficina.infrastructure.persistence.jpa.entity.FuncionarioEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FuncionarioJpaRepository : JpaRepository<FuncionarioEntity, UUID> {
    fun findByNome(nome: String): FuncionarioEntity?
}
