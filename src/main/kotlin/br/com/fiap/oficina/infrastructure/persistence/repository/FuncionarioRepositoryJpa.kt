package br.com.fiap.oficina.infrastructure.persistence.repository

import br.com.fiap.oficina.infrastructure.persistence.entity.FuncionarioEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface FuncionarioRepositoryJpa : JpaRepository<FuncionarioEntity, UUID>
